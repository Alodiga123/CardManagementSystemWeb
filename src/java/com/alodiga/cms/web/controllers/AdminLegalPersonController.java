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
import com.cms.commons.models.EconomicActivity;
import com.cms.commons.models.LegalPerson;
import com.cms.commons.models.PhonePerson;
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
    private Textbox txtIdentificationNumber;
    private Textbox txtTradeName;
    private Textbox txtEnterpriseName;
    private Textbox txtPhoneNumber;
    private Textbox txtRegistryNumber;
    private Textbox txtPaidInCapital;
    private Combobox cmbCountry;
    private Combobox cmbDocumentsPersonType;
    private Combobox cmbEconomicActivity;
    private Datebox txtExpirationDate;
    private Datebox txtDateInscriptionRegister;
    private UtilsEJB utilsEJB = null;
    private LegalPerson legalPersonParam;
    private Button btnSave;
    private Integer eventType;
    
    private Tabbox tb;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        legalPersonParam = (Sessions.getCurrent().getAttribute("object") != null) ? (LegalPerson) Sessions.getCurrent().getAttribute("object") : null;
        //eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        eventType = 1;
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
        txtIdentificationNumber.setRawValue(null);
        txtTradeName.setRawValue(null);
        txtEnterpriseName.setRawValue(null);
        txtPhoneNumber.setRawValue(null);
        txtRegistryNumber.setRawValue(null);
        txtPaidInCapital.setRawValue(null);
    }

    private void loadFields(LegalPerson legalPerson) {
        try {
            txtTradeName.setText(legalPerson.getTradeName());
            txtEnterpriseName.setText(legalPerson.getEnterpriseName());
            txtDateInscriptionRegister.setValue(legalPerson.getDateInscriptionRegister());
            txtPhoneNumber.setValue(legalPerson.getPersonId().getPhonePerson().getNumberPhone());
            txtRegistryNumber.setText(legalPerson.getRegisterNumber());
            txtPaidInCapital.setText(legalPerson.getPayedCapital().toString());
            txtIdentificationNumber.setText(legalPerson.getIdentificationNumber());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtIdentificationNumber.setReadonly(true);
        txtTradeName.setReadonly(true);
        txtEnterpriseName.setReadonly(true);
        txtPhoneNumber.setReadonly(true);
        txtRegistryNumber.setReadonly(true);
        txtPaidInCapital.setReadonly(true);
        txtExpirationDate.setDisabled(true);
        txtDateInscriptionRegister.setDisabled(true);
        cmbCountry.setDisabled(true);
        cmbDocumentsPersonType.setDisabled(true);
        cmbEconomicActivity.setDisabled(true);
        
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtIdentificationNumber.getText().isEmpty()) {
            txtIdentificationNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtTradeName.getText().isEmpty()) {
            txtTradeName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtEnterpriseName.getText().isEmpty()) {
            txtEnterpriseName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtPhoneNumber.getText().isEmpty()) {
            txtPhoneNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtRegistryNumber.getText().isEmpty()) {
            txtRegistryNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtPaidInCapital.getText().isEmpty()) {
            txtPaidInCapital.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;

    }

    public void onClick$btnCodes() {
        Executions.getCurrent().sendRedirect("/docs/T-SP-E.164D-2009-PDF-S.pdf", "_blank");
    }

    private void saveLegalPerson(LegalPerson _legalPerson) {
        try {
            LegalPerson legalPerson = null;

            if (_legalPerson != null) {
                legalPerson = _legalPerson;
            } else {//New LegalPerson
                legalPerson = new LegalPerson();
            }
            legalPerson.setIdentificationNumber(txtIdentificationNumber.getText());
            legalPerson.setTradeName(txtTradeName.getText());
            legalPerson.setEnterpriseName(txtEnterpriseName.getText());
            //legalPerson.setPersonId((PhonePerson));
            legalPerson.setRegisterNumber(txtRegistryNumber.getText());
            
            //legalPerson.setPayedCapital(txtPaidInCapital.getText());
            
            if(txtDateInscriptionRegister.getValue()!=null){
                    legalPerson.setDateInscriptionRegister(new Timestamp(txtDateInscriptionRegister.getValue().getTime()));
            }else{
                    legalPerson.setDateInscriptionRegister(new Timestamp(new Date().getTime()));
            }
            
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
                loadCmbEconomicActivity(eventType);
                loadCmbDocumentsPersonType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(legalPersonParam);                                
                txtIdentificationNumber.setDisabled(true);
                txtTradeName.setDisabled(true);
                txtEnterpriseName.setDisabled(true);
                txtPhoneNumber.setDisabled(true);
                txtRegistryNumber.setDisabled(true);
                txtPaidInCapital.setDisabled(true);
                txtExpirationDate.setDisabled(true);
                txtDateInscriptionRegister.setDisabled(true);
                loadCmbCountry(eventType);
                loadCmbEconomicActivity(eventType);
                loadCmbDocumentsPersonType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbEconomicActivity(eventType);
                loadCmbDocumentsPersonType(eventType);
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(legalPersonParam != null ? legalPersonParam.getPersonId().getCountryId().getId() : 0));
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
            documentsPersonType = utilsEJB.getDocumentsPersonTypes(request1);
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger,Long.valueOf(legalPersonParam != null ? legalPersonParam.getDocumentsPersonTypeId().getId() : 0));
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
    
      
    
    private void loadCmbEconomicActivity(Integer evenInteger) {
        //cmbEconomicActivity
        EJBRequest request = new EJBRequest();
        List<EconomicActivity> economicActivity;

        try {
            economicActivity = utilsEJB.getEconomicActivitys(request);
            loadGenericCombobox(economicActivity, cmbEconomicActivity, "description", evenInteger, Long.valueOf(legalPersonParam != null ? legalPersonParam.getEconomicActivityId().getId() : 0));
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
