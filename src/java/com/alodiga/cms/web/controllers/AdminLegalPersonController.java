package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.LegalPerson;
import com.cms.commons.models.PersonType;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.impl.conn.Wire;
import org.codehaus.groovy.tools.shell.Command;
import org.jboss.weld.metadata.Selectors;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;

public class AdminLegalPersonController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentification;
    private Textbox txtTradeName;
    private Textbox txtRif;
    private Textbox txtEnterpriseName;
    private Textbox txtEconomicActivity;
    private Textbox txtLegalAddress;
    private Textbox txtRegistryNumber;
    private Textbox txtPaidInCapital;
    private Textbox txtCapitalCurrency;
    private Combobox cmbCountry;
    private Combobox cmbDocumentsPersonType;
    private Combobox cmbPersonType;
    private Combobox cmbEconomicActivity;
    private Datebox txtExpirationDate;
    private Datebox txtRegistrationDate;
    private UtilsEJB utilsEJB = null;
    private LegalPerson legalPersonParam;
    private Button btnSave;
    private Integer eventType;
    
    private Tabbox tb;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        legalPersonParam = (Sessions.getCurrent().getAttribute("object") != null) ? (LegalPerson) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
        //initView(eventType, "sp.crud.country");
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtIdentification.setRawValue(null);
        txtTradeName.setRawValue(null);
        txtRif.setRawValue(null);
        txtEnterpriseName.setRawValue(null);
        txtEconomicActivity.setRawValue(null);
        txtLegalAddress.setRawValue(null);
        txtRegistryNumber.setRawValue(null);
        txtPaidInCapital.setRawValue(null);
        txtCapitalCurrency.setRawValue(null);
    }

    private void loadFields(LegalPerson legalPerson) {
        try {
            txtTradeName.setText(legalPerson.getTradeName());
            txtEnterpriseName.setText(legalPerson.getEnterpriseName());
            txtRegistrationDate.setValue(legalPerson.getDateInscriptionRegister());
            txtRegistryNumber.setText(legalPerson.getRegisterNumber());
            txtPaidInCapital.setText(legalPerson.getPayedCapital().toString());
            txtEconomicActivity.setText(legalPerson.getEconomicActivityId().getDescription());
            txtIdentification.setText(legalPerson.getIdentificationNumber());
            
            //txtRif.setText(legalPerson.getPersonId());
            
            
            //txtLegalAddress.setText(legalPerson.get);
            
            
            //txtCapitalCurrency.setText(legalPerson.get);
        
            
            //txtExpirationDate.setValue(legalPerson.get);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtIdentification.setReadonly(true);
        txtTradeName.setReadonly(true);
        txtRif.setReadonly(true);
        txtEnterpriseName.setReadonly(true);
        txtEconomicActivity.setReadonly(true);
        txtLegalAddress.setReadonly(true);
        txtRegistryNumber.setReadonly(true);
        txtPaidInCapital.setReadonly(true);
        txtCapitalCurrency.setReadonly(true);
        cmbCountry.setDisabled(true);
        cmbDocumentsPersonType.setDisabled(true);
        cmbPersonType.setDisabled(true);
        txtExpirationDate.setDisabled(true);
        txtRegistrationDate.setDisabled(true);
        
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtIdentification.getText().isEmpty()) {
            txtIdentification.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtTradeName.getText().isEmpty()) {
            txtTradeName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtEnterpriseName.getText().isEmpty()) {
            txtEnterpriseName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtEconomicActivity.getText().isEmpty()) {
            txtEconomicActivity.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtLegalAddress.getText().isEmpty()) {
            txtLegalAddress.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtRegistryNumber.getText().isEmpty()) {
            txtRegistryNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtPaidInCapital.getText().isEmpty()) {
            txtPaidInCapital.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtCapitalCurrency.getText().isEmpty()) {
            txtCapitalCurrency.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);

        } else {
            return true;
        }
        return false;

    }

    public void onClick$btnCodes() {
        Executions.getCurrent().sendRedirect("/docs/T-SP-E.164D-2009-PDF-S.pdf", "_blank");
    }

    public void onClick$btnShortNames() {
        Executions.getCurrent().sendRedirect("/docs/countries-abbreviation.pdf", "_blank");
    }

    private void saveLegalPerson(LegalPerson _legalPerson) {
        try {
            LegalPerson legalPerson = null;

            if (_legalPerson != null) {
                legalPerson = _legalPerson;
            } else {//New country
                legalPerson = new LegalPerson();
            }
            
            /*
            
            txtTradeName.setText(legalPerson.getTradeName());
            txtEnterpriseName.setText(legalPerson.getEnterpriseName());
            txtRegistrationDate.setValue(legalPerson.getDateInscriptionRegister());
            txtRegistryNumber.setText(legalPerson.getRegisterNumber());
            txtPaidInCapital.setText(legalPerson.getPayedCapital().toString());
            txtEconomicActivity.setText(legalPerson.getEconomicActivityId().getDescription());
            txtIdentification.setText(legalPerson.getIdentificationNumber());
            */
            
           
            
            
            
            legalPerson.setTradeName(txtTradeName.getText());
            legalPerson.setEnterpriseName(txtEnterpriseName.getText());
            if(txtRegistrationDate.getValue()!=null){
                    legalPerson.setDateInscriptionRegister(new Timestamp(txtRegistrationDate.getValue().getTime()));
            }else{
                    legalPerson.setDateInscriptionRegister(new Timestamp(new Date().getTime()));
            }
            legalPerson.setRegisterNumber(txtRegistryNumber.getText());
            //legalPerson.setPayedCapital(txtPaidInCapital.getValue());
            //legalPerson.setEconomicActivityId(txtEconomicActivity.get);
            legalPerson.setIdentificationNumber(txtIdentification.getText());
            
            
            
            
            //legalPerson.setIdentificationNumber(txtRif.getText());
            
            //legalPerson.setEconomicActivityId(txtComercialActivity.getText());
            //legalPerson.set(txtLegalAddress.getText());
            legalPerson.setIdentificationNumber(txtRegistryNumber.getText());
            legalPerson.setIdentificationNumber(txtPaidInCapital.getText());
            legalPerson.setIdentificationNumber(txtCapitalCurrency.getText());
            //country.setCurrencyId((Currency) cmbCurrency.getSelectedItem().getValue());//prueba
            //legalPerson.setPersonId((Country) cmbCountry.getSelectedItem().getDescription());
            legalPerson = utilsEJB.saveLegalPerson(legalPerson);
            legalPersonParam = legalPerson;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveLegalPerson(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveLegalPerson(legalPersonParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(legalPersonParam);
                loadCmbCountry(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(legalPersonParam);                                
                txtIdentification.setDisabled(true);
                txtTradeName.setDisabled(true);
                txtRif.setDisabled(true);
                txtEnterpriseName.setDisabled(true);
                txtEconomicActivity.setDisabled(true);
                txtLegalAddress.setDisabled(true);
                txtRegistryNumber.setDisabled(true);
                txtPaidInCapital.setDisabled(true);
                txtCapitalCurrency.setDisabled(true);
                loadCmbCountry(eventType);
                loadCmbDocumentsPersonType(eventType);
                loadcmbPersonType(eventType);
                txtExpirationDate.setDisabled(true);
                txtRegistrationDate.setDisabled(true);
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(legalPersonParam != null ? legalPersonParam.getId() : 0));
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
    
    
    private void loadcmbEconomicActivity(Integer evenInteger) {
        //cmbCountry
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;

        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries, cmbEconomicActivity, "description", evenInteger, Long.valueOf(legalPersonParam != null ? legalPersonParam.getId() : 0));
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
    
    
    
    

    
    private void loadCmbDocumentsPersonType(Integer evenInteger) {
        //cmbDocumentsPersonType
        EJBRequest request1 = new EJBRequest();
        List<DocumentsPersonType> documentsPersonType;

        try {
            documentsPersonType = utilsEJB.getDocumentsPersonType(request1);
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger,(legalPersonParam != null ? legalPersonParam.getId() : 0));
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
    
    private void loadcmbPersonType(Integer evenInteger) {
        //cmbPersonType
        EJBRequest request1 = new EJBRequest();
        List<PersonType> personType;

        try {
            personType = utilsEJB.getPersonTypes(request1);
            loadGenericCombobox(personType, cmbPersonType, "description", evenInteger, Long.valueOf(legalPersonParam != null ? legalPersonParam.getId() : 0));
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

    public class TabboxVM {

        Tabbox tb_tabbox;

        public void window1() {
            agregarTab("Ventana 1", "v1", "v1.zul");
        }

        public void window2() {
            agregarTab("Ventana 2", "v2", "v2.zul");
        }

        public void window3() {
            agregarTab("Ventana 3", "v3", "v3.zul");
        }

        private void agregarTab(String titulo, String id, String zul) {
            Map<String, Object> arguments = new HashMap<String, Object>();
            Tabpanel tabpanel = new Tabpanel();
            Tab tab = new Tab(titulo);
            tab.setId(id);

            tab.setClosable(true);
            tab.setSelected(true);

            if (tb_tabbox.getTabs() == null) {
                tb_tabbox.appendChild(new Tabs());
                tb_tabbox.appendChild(new Tabpanels());
            }

            tb_tabbox.getTabs().appendChild(tab);

            arguments.put("tabularIndex", tab.getIndex());

            tb_tabbox.getTabpanels().appendChild(tabpanel);
            tb_tabbox.invalidate();

            Executions.createComponents(zul, tabpanel, arguments);
        }

    }
}
