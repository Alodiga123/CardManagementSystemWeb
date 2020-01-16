package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Transaction;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class AdminTransactionController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private ProductEJB productEJB = null;
    private Textbox txtCodeTransaction;
    private Textbox txtDescriptionTransaction;
    private Radio rMonetaryTypeYes;
    private Radio rMonetaryTypeNo;
    private Transaction transactionParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;
        
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        Sessions.getCurrent();
        transactionParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Transaction) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.transaction.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.transaction.view"));
                break;
            default:
                break;
        }
        try {
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtCodeTransaction.setRawValue(null);
        txtDescriptionTransaction.setRawValue(null);
    }
    
    private void loadFields(Transaction transaction) {
        try {
            txtCodeTransaction.setText(transaction.getCode().toString());
        } catch (Exception ex) {
            showError(ex);
        }
        try {
            txtDescriptionTransaction.setText(transaction.getDescription().toString());
        } catch (Exception ex) {
            showError(ex);
        }
        try {
            if (transaction.getIndMonetaryType() == 1) {
                rMonetaryTypeYes.setChecked(true);
            } else {
                rMonetaryTypeNo.setChecked(true);
            }
            
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtCodeTransaction.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtCodeTransaction.getText().isEmpty()) {
            txtCodeTransaction.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;

    }

    private void saveTransaction(Transaction _transaction) {
        short indMonetaryType = 0;
        try {
            Transaction transaction = null;
            if (_transaction != null) {
                transaction = _transaction;
            } else {//New personType
                transaction = new Transaction();
            }
            
            if (rMonetaryTypeYes.isChecked()) {
                indMonetaryType = 1;
            } else {
                indMonetaryType = 0;
            }
            transaction.setCode(txtCodeTransaction.getText());
            transaction.setDescription(txtDescriptionTransaction.getText());
            transaction.setIndMonetaryType(indMonetaryType);
            transaction = productEJB.saveTransaction(transaction);
            transactionParam = transaction;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveTransaction(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveTransaction(transactionParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(transactionParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(transactionParam);
                txtCodeTransaction.setDisabled(true);
                txtDescriptionTransaction.setDisabled(true);
                break;
            case WebConstants.EVENT_ADD:
                loadFields(transactionParam);
                break;
            default:
                break;
        }
    }

    
}