package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Channel;
import com.cms.commons.models.Country;
import com.cms.commons.models.GeneralRate;
import com.cms.commons.models.ProductType;
import com.cms.commons.models.RateApplicationType;
import com.cms.commons.models.Transaction;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;

import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class AdminGeneralRateController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private GeneralRate generalRateParam;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB = null;
    private Combobox cmbCountry;
    private Combobox cmbProductType;
    private Combobox cmbChannel;
    private Combobox cmbTransaction;
    private Combobox cmbRateApplicationType;
    private Doublebox txtFixedRate;
    private Doublebox txtPercentageRate;
    private Intbox txtTotalTransactionInitialExempt;
    private Intbox txtTotalTransactionExemptPerMonth;
    private Radio rFixedRateYes;
    private Radio rFixedRateNo;
    private Radio rModificationCardHolderYes;
    private Radio rModificationCardHolderNo;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    public Tabbox tb;
    public Window winAdminGeneralRate;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            generalRateParam = null;
        } else {
            generalRateParam = (GeneralRate) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadFields(GeneralRate generalRate) {
        try {
            txtFixedRate.setText(generalRate.getFixedRate().toString());
            txtPercentageRate.setText(generalRate.getPercentageRate().toString());
            txtTotalTransactionInitialExempt.setText(generalRate.getTotalInitialTransactionsExempt().toString());
            txtTotalTransactionExemptPerMonth.setText(generalRate.getTotalTransactionsExemptPerMonth().toString());
            if (generalRate.getIndCardHolderModification() == true) {
                rModificationCardHolderYes.setChecked(true);
            } else {
                rModificationCardHolderNo.setChecked(true);
            }
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$rFixedRateYes() {
        txtFixedRate.setDisabled(false);
        txtPercentageRate.setDisabled(true);
    }

    public void onClick$rFixedRateNo() {
        txtFixedRate.setDisabled(true);
        txtPercentageRate.setDisabled(false);
    }

    public Boolean validateEmpty() {
        if (txtTotalTransactionInitialExempt.getText().isEmpty()) {
            txtTotalTransactionInitialExempt.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtTotalTransactionExemptPerMonth.getText().isEmpty()) {
            txtTotalTransactionExemptPerMonth.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }

    public void blockFields() {
        btnSave.setVisible(false);
    }

    private void saveGeneralRate(GeneralRate _generalRate) {
        try {
            boolean indModificationCardHolder = true;
            GeneralRate generalRate = null;

            if (_generalRate != null) {
                generalRate = _generalRate;
            } else {//New Request
                generalRate = new GeneralRate();
            }

            if (rModificationCardHolderYes.isChecked()) {
                indModificationCardHolder = true;
            } else {
                indModificationCardHolder = true;
            }

            //Guarda las tarifas generales en la BD
            generalRate.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            generalRate.setProductTypeId((ProductType) cmbProductType.getSelectedItem().getValue());
            generalRate.setChannelId((Channel) cmbChannel.getSelectedItem().getValue());
            generalRate.setTransactionId((Transaction) cmbTransaction.getSelectedItem().getValue());
            if (rFixedRateYes != null) {
                generalRate.setFixedRate(txtFixedRate.getValue().floatValue());
            } else if (rFixedRateNo != null) {
                generalRate.setPercentageRate(txtPercentageRate.getValue().floatValue());
            }
            generalRate.setTotalInitialTransactionsExempt(txtTotalTransactionInitialExempt.getValue());
            generalRate.setTotalTransactionsExemptPerMonth(txtTotalTransactionExemptPerMonth.getValue());
            generalRate.setRateApplicationTypeId((RateApplicationType) cmbRateApplicationType.getSelectedItem().getValue());
            generalRate.setIndCardHolderModification(indModificationCardHolder);
            generalRate = productEJB.saveGeneralRate(generalRate);
            generalRateParam = generalRate;
            this.showMessage("sp.common.save.success", false, null);
            EventQueues.lookup("updateGeneralRate", EventQueues.APPLICATION, true).publish(new Event(""));

            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveGeneralRate(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveGeneralRate(generalRateParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void onClick$btnBack() {
        winAdminGeneralRate.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(generalRateParam);
                loadCmbCountry(eventType);
                loadCmbProductType(eventType);
                loadCmbChannel(eventType);
                loadCmbTransaction(eventType);
                loadCmbRateApplicationType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(generalRateParam);
                loadCmbCountry(eventType);
                loadCmbProductType(eventType);
                loadCmbChannel(eventType);
                loadCmbTransaction(eventType);
                loadCmbRateApplicationType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbTransaction(eventType);
                loadCmbCountry(eventType);
                loadCmbProductType(eventType);
                loadCmbChannel(eventType);
                loadCmbRateApplicationType(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbCountry(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;
        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(generalRateParam != null ? generalRateParam.getCountryId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }

    private void loadCmbProductType(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<ProductType> productTypes;
        try {
            productTypes = utilsEJB.getProductTypes(request1);
            loadGenericCombobox(productTypes, cmbProductType, "name", evenInteger, Long.valueOf(generalRateParam != null ? generalRateParam.getProductTypeId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }

    private void loadCmbChannel(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Channel> channelList;
        try {
            channelList = productEJB.getChannel(request1);
            loadGenericCombobox(channelList, cmbChannel, "name", evenInteger, Long.valueOf(generalRateParam != null ? generalRateParam.getChannelId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }

    private void loadCmbTransaction(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Transaction> transactionList;
        try {
            transactionList = productEJB.getTransaction(request1);
            loadGenericCombobox(transactionList, cmbTransaction, "description", evenInteger, Long.valueOf(generalRateParam != null ? generalRateParam.getTransactionId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }

    private void loadCmbRateApplicationType(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<RateApplicationType> rateApplicationTypeList;
        try {
            rateApplicationTypeList = productEJB.getRateApplicationType(request1);
            loadGenericCombobox(rateApplicationTypeList, cmbRateApplicationType, "description", evenInteger, Long.valueOf(generalRateParam != null ? generalRateParam.getRateApplicationTypeId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }

}
