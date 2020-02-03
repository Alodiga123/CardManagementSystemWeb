package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Channel;
import com.cms.commons.models.Country;
import com.cms.commons.models.GeneralRate;
import com.cms.commons.models.ProductType;
import com.cms.commons.models.RateApplicationType;
import com.cms.commons.models.RateByProgram;
import com.cms.commons.models.Transaction;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;

import org.zkoss.zul.Toolbarbutton;

public class AdminRateByProgramController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private RateByProgram rateByProgramParam;
    public static RateByProgram rateProgram = null;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB = null;
    private Label lblProgram;
    private Label lblProductType;
    private Label lblChannel;
    private Label lblTransaction;
    private Label lblRateApplicationType;
    private Textbox txtFixedRate;
    private Textbox txtPercentageRate;
    private Textbox txtTotalTransactionInitialExempt;
    private Textbox txtTotalTransactionExemptPerMonth;
    private Radio rModificationCardHolderYes;
    private Radio rModificationCardHolderNo;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    public Tabbox tb;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        rateByProgramParam = (Sessions.getCurrent().getAttribute("object") != null) ? (RateByProgram) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.rateByProgram.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.rateByProgram.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.rateByProgram.add"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public RateByProgram getRateByProgram() {
        return rateProgram;
    }
    
    public void clearFields() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadFields(RateByProgram rateByProgram) {
        try {
            lblProgram.setValue(rateByProgram.getProgramId().getName());
            lblProductType.setValue(rateByProgram.getProgramId().getProductTypeId().getName());
            lblChannel.setValue(rateByProgram.getChannelId().getName());
            lblTransaction.setValue(rateByProgram.getTransactionId().getDescription());
            lblRateApplicationType.setValue(rateByProgram.getRateApplicationTypeId().getDescription());
            txtFixedRate.setText(rateByProgram.getFixedRate().toString());
            txtPercentageRate.setText(rateByProgram.getPercentageRate().toString());
            txtTotalTransactionInitialExempt.setText(rateByProgram.getTotalInitialTransactionsExempt().toString());
            txtTotalTransactionExemptPerMonth.setText(rateByProgram.getTotalTransactionsExemptPerMonth().toString());
            if (rateByProgram.getIndCardHolderModification() == true) {
                rModificationCardHolderYes.setChecked(true);
            } else {
                rModificationCardHolderNo.setChecked(true);
            }
            
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        btnSave.setVisible(false);
    }

    private void saveRateByProgram(RateByProgram _rateByProgram) {
        try {
            boolean indModificationCardHolder = true;
            RateByProgram rateByProgram = null;

            if (_rateByProgram != null) {
                rateByProgram = _rateByProgram;
            } else {//New Request
                rateByProgram = new RateByProgram();
            }
            
            if (rModificationCardHolderYes.isChecked()) {
                indModificationCardHolder = true;
            } else {
                indModificationCardHolder = true;
            }
            
            //Guarda las tarifas del programa en la BD
            rateByProgram.setProgramId(rateByProgram.getProgramId());
            rateByProgram.setChannelId(rateByProgram.getChannelId());
            rateByProgram.setTransactionId(rateByProgram.getTransactionId());
            rateByProgram.setFixedRate(Float.parseFloat(txtFixedRate.getText()));
            rateByProgram.setPercentageRate(Float.parseFloat(txtPercentageRate.getText()));
            rateByProgram.setTotalInitialTransactionsExempt(Integer.parseInt(txtTotalTransactionInitialExempt.getText()));
            rateByProgram.setTotalTransactionsExemptPerMonth(Integer.parseInt(txtTotalTransactionExemptPerMonth.getText()));
            rateByProgram.setRateApplicationTypeId(rateByProgram.getRateApplicationTypeId());
            rateByProgram.setIndCardHolderModification(indModificationCardHolder);
            rateByProgram = productEJB.saveRateByProgram(rateByProgram);
            rateByProgramParam = rateByProgram;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveRateByProgram(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveRateByProgram(rateByProgramParam);
                break;
            default:
                break;
        }
    }
    
    public void onclick$btnBack() {
        Executions.getCurrent().sendRedirect("listRateByProgram.zul");
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(rateByProgramParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(rateByProgramParam);
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
    
}