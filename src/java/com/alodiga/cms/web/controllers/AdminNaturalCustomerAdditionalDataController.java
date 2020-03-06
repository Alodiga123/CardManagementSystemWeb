package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.AdditionalInformationNaturalCustomer;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.NaturalCustomer;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;

public class AdminNaturalCustomerAdditionalDataController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtFirstNames;
    private Textbox txtLastNames;
    private Textbox txtPhone;
    private Textbox txtEmail;
    private Textbox txtCarBrand;
    private Textbox txtCarModel;
    private Textbox txtCarYear;
    private Textbox txtCarPlate;
    private Textbox txtSalary;
    private Textbox txtProfession;
    private Textbox txtBonuses;
    private Textbox txtRentIncome;
    private Textbox txtOtherIncome;
    private Textbox txtTotalIncome;
    private Textbox txtHousingExpenses;
    private Textbox txtMonthlyRentMortgage;
    private Textbox txtMonthlyPaymentCreditCard;
    private Textbox txtMonthlyPaymentOtherCredit;
    private Textbox txtEducationExpenses;
    private Textbox txtTotalExpenses;
    private Combobox cmbCountry;
    private Combobox cmbDocumentsPersonType;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private Button btnSave;
    private Integer eventType;
    private NaturalCustomer naturalCustomer;
    public AdditionalInformationNaturalCustomer additionalInformationNaturalCustomerParam;
    private List<AdditionalInformationNaturalCustomer> additionalInformationList;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);

            AdminNaturalPersonCustomerController customer = new AdminNaturalPersonCustomerController();
            if (customer != null) {
                naturalCustomer = customer.getNaturalCustomer();

                Map params = new HashMap();
                EJBRequest request2 = new EJBRequest();
                params.put(Constants.NATURAL_CUSTOMER_KEY, naturalCustomer.getId());
                request2.setParams(params);
                additionalInformationList = personEJB.getAdditionalInformationNaturalCustomeByCustomer(request2);
            } else {
                additionalInformationList = null;
            }

            switch (eventType) {
                case WebConstants.EVENT_EDIT:
                    if (additionalInformationList != null) {
                        for (AdditionalInformationNaturalCustomer r : additionalInformationList) {
                            additionalInformationNaturalCustomerParam = r;
                        }
                    } else {
                        additionalInformationNaturalCustomerParam = null;
                    }
                    break;
                case WebConstants.EVENT_VIEW:
                    if (additionalInformationList != null) {
                        for (AdditionalInformationNaturalCustomer r : additionalInformationList) {
                            additionalInformationNaturalCustomerParam = r;
                        }
                    } else {
                        additionalInformationNaturalCustomerParam = null;
                    }
                    break;
                case WebConstants.EVENT_ADD:
                    additionalInformationNaturalCustomerParam = null;
                    break;
            }
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onChange$cmbCountry() {
        cmbDocumentsPersonType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbDocumentsPersonType(eventType, country.getId());
    }

    public void clearFields() {
        txtFirstNames.setRawValue(null);
        txtLastNames.setRawValue(null);
        txtIdentificationNumber.setRawValue(null);
        txtPhone.setRawValue(null);
        txtEmail.setRawValue(null);
        txtCarBrand.setRawValue(null);
        txtCarModel.setRawValue(null);
        txtCarYear.setRawValue(null);
        txtCarPlate.setRawValue(null);
        txtSalary.setRawValue(null);
        txtProfession.setRawValue(null);
        txtBonuses.setRawValue(null);
        txtRentIncome.setRawValue(null);
        txtOtherIncome.setRawValue(null);
        txtTotalIncome.setRawValue(null);
        txtHousingExpenses.setRawValue(null);
        txtMonthlyRentMortgage.setRawValue(null);
        txtMonthlyPaymentCreditCard.setRawValue(null);
        txtMonthlyPaymentOtherCredit.setRawValue(null);
        txtEducationExpenses.setRawValue(null);
        txtTotalExpenses.setRawValue(null);
    }

    private void loadFields(AdditionalInformationNaturalCustomer additionalInformationNaturalCustomer) {
        try {
            txtFirstNames.setText(additionalInformationNaturalCustomer.getFirstNamesHusband());
            txtLastNames.setText(additionalInformationNaturalCustomer.getLastNamesHusband());
            txtIdentificationNumber.setText(additionalInformationNaturalCustomer.getIdentificationNumberHusband());
            txtPhone.setText(additionalInformationNaturalCustomer.getPhoneHusband());
            txtEmail.setText(additionalInformationNaturalCustomer.getEmailHusband());
            txtCarBrand.setText(additionalInformationNaturalCustomer.getCarBrand());
            txtCarModel.setText(additionalInformationNaturalCustomer.getCarModel());
            txtCarYear.setText(additionalInformationNaturalCustomer.getCarYear().toString());
            txtCarPlate.setText(additionalInformationNaturalCustomer.getCarPlate());
            txtSalary.setText(additionalInformationNaturalCustomer.getSalary().toString());
            txtProfession.setText(additionalInformationNaturalCustomer.getFreeProfessionalExercise().toString());
            txtBonuses.setText(additionalInformationNaturalCustomer.getBonusesCommissionsFee().toString());
            txtRentIncome.setText(additionalInformationNaturalCustomer.getRentsIncome().toString());
            txtOtherIncome.setText(additionalInformationNaturalCustomer.getOtherIncome().toString());
            txtTotalIncome.setText(additionalInformationNaturalCustomer.getTotalIncome().toString());
            txtHousingExpenses.setText(additionalInformationNaturalCustomer.getHousingExpenses().toString());
            txtMonthlyRentMortgage.setText(additionalInformationNaturalCustomer.getMonthlyRentMortgage().toString());
            txtMonthlyPaymentCreditCard.setText(additionalInformationNaturalCustomer.getMonthlyPaymentCreditCard().toString());
            txtMonthlyPaymentOtherCredit.setText(additionalInformationNaturalCustomer.getMonthlyPaymentOtherCredit().toString());
            txtEducationExpenses.setText(additionalInformationNaturalCustomer.getEducationExpenses().toString());
            txtTotalExpenses.setText(additionalInformationNaturalCustomer.getTotalExpenses().toString());

            additionalInformationNaturalCustomerParam = additionalInformationNaturalCustomer;
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtFirstNames.setReadonly(true);
        txtLastNames.setReadonly(true);
        txtIdentificationNumber.setReadonly(true);
        txtPhone.setReadonly(true);
        txtEmail.setReadonly(true);
        txtCarBrand.setReadonly(true);
        txtCarModel.setReadonly(true);
        txtCarYear.setReadonly(true);
        txtCarPlate.setReadonly(true);
        txtSalary.setReadonly(true);
        txtProfession.setReadonly(true);
        txtBonuses.setReadonly(true);
        txtRentIncome.setReadonly(true);
        txtOtherIncome.setReadonly(true);
        txtTotalIncome.setReadonly(true);
        txtHousingExpenses.setReadonly(true);
        txtMonthlyRentMortgage.setReadonly(true);
        txtMonthlyPaymentCreditCard.setReadonly(true);
        txtMonthlyPaymentOtherCredit.setReadonly(true);
        txtEducationExpenses.setReadonly(true);
        txtTotalExpenses.setReadonly(true);
        cmbCountry.setReadonly(true);

        btnSave.setVisible(false);
    }

    private void saveNaturalPersonCustomer(AdditionalInformationNaturalCustomer _addiAdditionalInformationNaturalCustomer) {
        NaturalCustomer naturalCustomer = null;
        AdminNaturalPersonCustomerController adminNaturalCustomer = new AdminNaturalPersonCustomerController();
        try {
            AdditionalInformationNaturalCustomer additionalInformationNaturalCustomer = null;

            if (_addiAdditionalInformationNaturalCustomer != null) {
                additionalInformationNaturalCustomer = _addiAdditionalInformationNaturalCustomer;
            } else {//New ApplicantNaturalPerson
                additionalInformationNaturalCustomer = new AdditionalInformationNaturalCustomer();
            }

            //Cliente
            if (adminNaturalCustomer.getNaturalCustomer() != null) {
                naturalCustomer = adminNaturalCustomer.getNaturalCustomer();
            }

            //additionalInformationNaturalCustomer            
            additionalInformationNaturalCustomer.setNaturalCustomerId(naturalCustomer);
            additionalInformationNaturalCustomer.setFirstNamesHusband(txtFirstNames.getText());
            additionalInformationNaturalCustomer.setLastNamesHusband(txtLastNames.getText());
            additionalInformationNaturalCustomer.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            additionalInformationNaturalCustomer.setIdentificationNumberHusband(txtIdentificationNumber.getText());
            additionalInformationNaturalCustomer.setPhoneHusband(txtPhone.getText());
            additionalInformationNaturalCustomer.setEmailHusband(txtEmail.getText());
            additionalInformationNaturalCustomer.setCarBrand(txtCarBrand.getText());
            additionalInformationNaturalCustomer.setCarModel(txtCarModel.getText());
            additionalInformationNaturalCustomer.setCarYear(Integer.parseInt(txtCarYear.getText()));
            additionalInformationNaturalCustomer.setCarPlate(txtCarPlate.getValue());
            additionalInformationNaturalCustomer.setSalary(Float.parseFloat(txtSalary.getText()));
            additionalInformationNaturalCustomer.setFreeProfessionalExercise(Float.parseFloat(txtProfession.getText()));
            additionalInformationNaturalCustomer.setBonusesCommissionsFee(Float.parseFloat(txtBonuses.getText()));
            additionalInformationNaturalCustomer.setRentsIncome(Float.parseFloat(txtRentIncome.getText()));
            additionalInformationNaturalCustomer.setOtherIncome(Float.parseFloat(txtOtherIncome.getText()));
            additionalInformationNaturalCustomer.setHousingExpenses(Float.parseFloat(txtHousingExpenses.getText()));
            additionalInformationNaturalCustomer.setMonthlyRentMortgage(Float.parseFloat(txtMonthlyRentMortgage.getText()));
            additionalInformationNaturalCustomer.setMonthlyPaymentCreditCard(Float.parseFloat(txtMonthlyPaymentCreditCard.getText()));
            additionalInformationNaturalCustomer.setMonthlyPaymentOtherCredit(Float.parseFloat(txtMonthlyPaymentOtherCredit.getText()));
            additionalInformationNaturalCustomer.setEducationExpenses(Float.parseFloat(txtEducationExpenses.getText()));
            additionalInformationNaturalCustomer.setTotalExpenses(Float.parseFloat(txtTotalExpenses.getText()));
            additionalInformationNaturalCustomer.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            additionalInformationNaturalCustomer = personEJB.saveAdditionalInformationNaturalCustomer(additionalInformationNaturalCustomer);
            this.showMessage("sp.common.save.success", false, null);

            additionalInformationNaturalCustomerParam = additionalInformationNaturalCustomer;

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveNaturalPersonCustomer(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveNaturalPersonCustomer(additionalInformationNaturalCustomerParam);
                break;
            default:
                break;
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                if (additionalInformationNaturalCustomerParam != null) {
                    loadFields(additionalInformationNaturalCustomerParam);
                } else {
                    additionalInformationNaturalCustomerParam = null;
                }
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_VIEW:
                blockFields();
                if (additionalInformationNaturalCustomerParam != null) {
                    loadFields(additionalInformationNaturalCustomerParam);
                } else {
                    additionalInformationNaturalCustomerParam = null;
                }
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbCountry(Integer evenInteger) {
        //cmbCountry
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;

        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(additionalInformationNaturalCustomerParam != null ? additionalInformationNaturalCustomerParam.getCountryId().getId() : 0));
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

    private void loadCmbDocumentsPersonType(Integer evenInteger, int countryId) {
        EJBRequest request1 = new EJBRequest();
        cmbDocumentsPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        params.put(QueryConstants.PARAM_IND_NATURAL_PERSON, WebConstants.IND_NATURAL_PERSON);
        request1.setParams(params);
        List<DocumentsPersonType> documentsPersonType;

        try {
            documentsPersonType = utilsEJB.getDocumentsPersonByCountry(request1);
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger, Long.valueOf(additionalInformationNaturalCustomerParam != null ? additionalInformationNaturalCustomerParam.getDocumentsPersonTypeId().getId() : 0));
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
