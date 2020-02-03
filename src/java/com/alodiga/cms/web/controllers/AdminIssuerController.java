package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Issuer;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.IssuerType;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonClassification;
import com.cms.commons.models.PersonType;
import com.cms.commons.util.Constants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminIssuerController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtName;
    private Textbox txtIssuerEmail;
    private Textbox txtBinNumber;
    private Textbox txtSwiftCode;
    private Textbox txtAbaCode;
    private Textbox txtContractNumber;
    private Textbox txtWebSite;
    private Textbox txtFaxNumber;
    private Textbox txtPersonContactName;
    private Textbox txtEmailPersonContact;
    private Combobox cmbCountry;
    private Combobox cmbPersonType;
    private Combobox cmbDocumentsPersonType;
    private Combobox cmbIssuerType;
    private Radio rActiveYes;
    private Radio rActiveNo;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private Issuer issuerParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        issuerParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Issuer) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.issuer.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.issuer.view"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    
    public void clearFields() {
        txtIdentificationNumber.setRawValue(null);
        txtName.setRawValue(null);
        txtIssuerEmail.setRawValue(null);
        txtBinNumber.setRawValue(null);
        txtSwiftCode.setRawValue(null);
        txtAbaCode.setRawValue(null);
        txtContractNumber.setRawValue(null);
        txtWebSite.setRawValue(null);
        txtFaxNumber.setRawValue(null);
        txtPersonContactName.setRawValue(null);
        txtEmailPersonContact.setRawValue(null);
    } 
    
        
    private void loadFields(Issuer issuer) {
        try {
            txtIdentificationNumber.setText(issuer.getDocumentIdentification());
            txtName.setText(issuer.getName());
            txtIssuerEmail.setText(issuer.getIssuerPersonId().getEmail());
            txtBinNumber.setText(issuer.getBinNumber().toString());
            txtSwiftCode.setText(issuer.getSwiftCode());
            txtAbaCode.setText(issuer.getAbaCode());
            txtContractNumber.setValue(issuer.getContractNumber());
            if (issuer.getWebSite() != null) {
                txtWebSite.setText(issuer.getWebSite());
            }
            if (issuer.getFaxNumber() != null) {
                txtFaxNumber.setText(issuer.getFaxNumber());
            }
            if (issuer.getPersonContactName() != null) {
                txtPersonContactName.setText(issuer.getPersonContactName());
            }    
            if (issuer.getEmailPersonContact() != null) {
                txtEmailPersonContact.setValue(issuer.getEmailPersonContact());                        
            }
            if (issuer.getStatusActive()== 1) {
                rActiveYes.setChecked(true);
            } else {
                rActiveNo.setChecked(true);
            }
        
        } catch (Exception ex) {
            showError(ex);
        }
    }     

    public void blockFields() {
        txtIdentificationNumber.setReadonly(true);
        txtName.setReadonly(true);
        txtIssuerEmail.setReadonly(true);
        txtBinNumber.setReadonly(true);
        txtSwiftCode.setReadonly(true);
        txtAbaCode.setReadonly(true);
        txtContractNumber.setReadonly(true);
        txtWebSite.setReadonly(true);
        txtFaxNumber.setReadonly(true);
        txtPersonContactName.setReadonly(true);
        txtEmailPersonContact.setReadonly(true);
        txtIdentificationNumber.setReadonly(true);
        btnSave.setVisible(false);
    }

    private void saveIssuer(Issuer _issuer) throws RegisterNotFoundException, NullParameterException, GeneralException {
        short indActive = 0;
        try {
            Issuer issuer = null;

            if (_issuer != null) {
                issuer = _issuer;
            } else {
                issuer = new Issuer();
            }

            if (rActiveYes.isChecked()) {
                indActive = 1;
            } else {
                indActive = 0;
            }

            //Obtener la clasificacion del emisor
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.CLASSIFICATION_PERSON_ISSUER);
            PersonClassification personClassification = utilsEJB.loadPersonClassification(request1);

            //Guardar la persona
            Person person = new Person();
            person.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            person.setPersonTypeId((PersonType) cmbPersonType.getSelectedItem().getValue());
            person.setEmail(txtIssuerEmail.getText());
            person.setCreateDate(new Timestamp(new Date().getTime()));
            person.setPersonClassificationId(personClassification);
            person = personEJB.savePerson(person);
            
            //Guarda el Emisor
            issuer.setName(txtName.getText());
            issuer.setBinNumber(Integer.parseInt(txtBinNumber.getText()));
            issuer.setSwiftCode(txtSwiftCode.getText());
            issuer.setAbaCode(txtAbaCode.getText());
            issuer.setContractNumber(txtContractNumber.getText());
            issuer.setFaxNumber(txtFaxNumber.getText());
            issuer.setPersonContactName(txtPersonContactName.getText());
            issuer.setEmailPersonContact(txtEmailPersonContact.getText());
            issuer.setStatusActive(indActive);
            issuer.setWebSite(txtWebSite.getText());
            issuer.setDocumentIdentification(txtIdentificationNumber.getText());
            issuer.setIssuerTypeId(((IssuerType) cmbIssuerType.getSelectedItem().getValue()));
            issuer.setIssuerPersonId(person);
            issuer.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            issuer = personEJB.saveIssuer(issuer);
            issuerParam =issuer;
            this.showMessage("sp.common.save.success", false, null);
        } catch (WrongValueException ex) {
            showError(ex);
        }
    }  
    
    public Boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtIdentificationNumber.getText().isEmpty()) {
            txtIdentificationNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtIssuerEmail.getText().isEmpty()) {
            txtIssuerEmail.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtBinNumber.getText().isEmpty()) {
            txtBinNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtSwiftCode.getText().isEmpty()) {
            txtSwiftCode.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtAbaCode.getText().isEmpty()) {
            txtAbaCode.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtContractNumber.getText().isEmpty()) {
            txtContractNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    
    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveIssuer(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveIssuer(issuerParam);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void onChange$cmbCountry() {
        cmbPersonType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbPersonType(eventType, country.getId());
    }
    
    public void onChange$cmbPersonType() {
        cmbDocumentsPersonType.setVisible(true);
        PersonType personType = (PersonType) cmbPersonType.getSelectedItem().getValue();
        loadCmbDocumentsPersonType(eventType, personType.getId());
    }
    
    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(issuerParam);
                loadCmbCountry(eventType);
                loadCmbIssuerType(eventType);
                onChange$cmbCountry();
                onChange$cmbPersonType();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(issuerParam);
                blockFields();            
                loadCmbCountry(eventType);
                loadCmbIssuerType(eventType);
                rActiveYes.setDisabled(true);
                rActiveNo.setDisabled(true);
                onChange$cmbCountry();
                onChange$cmbPersonType();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbIssuerType(eventType);
                break;
            default:
                break;
        }
    
    }    
    
    private void loadCmbPersonType(Integer evenInteger, Integer countryId) {
        EJBRequest request1 = new EJBRequest();
        List<PersonType> personType;
        try {
            personType = personEJB.getPersonType(request1);
            loadGenericCombobox(personType, cmbPersonType, "description", evenInteger, Long.valueOf(issuerParam != null ? issuerParam.getIssuerPersonId().getPersonTypeId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }

    }

    private void loadCmbDocumentsPersonType(Integer evenInteger, Integer documentPersonTypeId) {
        EJBRequest request1 = new EJBRequest();
        cmbDocumentsPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_DOCUMENT_PERSON_TYPE_ID, documentPersonTypeId);
        request1.setParams(params);
        List<DocumentsPersonType> documentsPersonType;
        try {
            documentsPersonType = personEJB.getDocumentsPersonType(request1);
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger, Long.valueOf(issuerParam != null ? issuerParam.getIssuerPersonId().getPersonTypeId().getId(): 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }

    }

    private void loadCmbIssuerType(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<IssuerType> issuerType;
        try {
            issuerType = personEJB.getIssuerType(request1);
            loadGenericCombobox(issuerType, cmbIssuerType, "description", evenInteger, Long.valueOf(issuerParam != null ? issuerParam.getIssuerTypeId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
    
    private void loadCmbCountry(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Country> country;
        try {
            country = utilsEJB.getCountries(request1);
            loadGenericCombobox(country, cmbCountry, "name", evenInteger, Long.valueOf(issuerParam != null ? issuerParam.getCountryId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private Object getSelectedItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

  }