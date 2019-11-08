package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.Currency;
import com.cms.commons.models.RequestType;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

public class AdminCurrencyController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtSimbol,txtName;
    private UtilsEJB utilsEJB = null;
    private Currency currencyParam;
    private Button btnSave;
    private Integer evenType2 = -1;
            
            


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        currencyParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Currency) Sessions.getCurrent().getAttribute("object") : null;
        evenType2 = (Integer) (Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE));
        initialize();
        loadData();
//      initView(eventType, "sp.crud.requestType");
    }

//    @Override
//    public void initView(int eventType, String adminView) {
//        super.initView(eventType, "sp.crud.requestType");
//    }
    @Override
    public void initialize() {
        super.initialize();
        try {

            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtSimbol.setRawValue(null);
        txtName.setRawValue(null);
    }

    private void loadFields(Currency currency) {
        try {
            txtSimbol.setText(currency.getSymbol());
            txtName.setText(currency.getName());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtSimbol.setReadonly(true);
        txtName.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        }
        if (txtSimbol.getText().isEmpty()) {
            txtSimbol.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;

    }


    private void saveCurrency(Currency currency_) {
        try {
            Currency currency = null;

            if (currency_ != null) {
                currency = currency_;
            } else {//New requestType
                currency = new Currency();
            }
            currency.setName(txtName.getText());
            currency.setSymbol(txtSimbol.getText());
            currency = utilsEJB.saveCurrency(currency);
            currencyParam = currency;
            this.showMessage("sp.common.save.success", false, null);
        } catch ( Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (evenType2) {
                case WebConstants.EVENT_ADD:
                    saveCurrency(null);
                break;
                case WebConstants.EVENT_EDIT:
                   saveCurrency(currencyParam);
                break;
            }
        }
    }

    public void loadData() {
        switch (evenType2) {
            case WebConstants.EVENT_EDIT:
                loadFields(currencyParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(currencyParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }

}
