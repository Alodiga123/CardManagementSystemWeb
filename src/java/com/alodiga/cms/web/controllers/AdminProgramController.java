package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Country;
import com.cms.commons.models.Currency;
import com.cms.commons.models.Issuer;
import com.cms.commons.models.BinSponsor;
import com.cms.commons.models.CardType;
import com.cms.commons.models.NaturalPerson;
import com.cms.commons.models.Person;
import com.cms.commons.models.Product;
import com.cms.commons.models.ProductType;
import com.cms.commons.models.Program;

import com.cms.commons.models.ProgramType;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;

public class AdminProgramController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox    txtName;
    private Textbox    txtdescription;
    private Datebox    dtbcontdate;
    private Combobox   cmbProgramType;
    private Combobox   cmbProductType;
    private Combobox   cmbIssuer;
    private Combobox   cmbProgramOwner;
    private Combobox   cmbBinSponsor;
    private Combobox   cmbCardType;
    private Textbox    website;
  //  private radiogroup branded;
   // private radiogroup reloadable;
    private Combobox   cmbSourceOfFound;
    private Textbox    txtother;
   // private radiogroup CashAcces;
  //  private radiogroup international;
    private Combobox   cmbNetWork;
    private Textbox    txtOtherNetWork;
    private Textbox    txtbin;
    private Combobox   cmbCurrency;
    private Combobox   cmbresponsibleNetwoork;
    private Textbox    txtotheBINN;
    private Combobox   cmbcardIssuanceType;
    private Datebox    dtbexpectedLaunchDate;
    private ProgramEJB programEJB = null;
    private final UtilsEJB utilsEJB = null;
    private Program programParam;
    private Button btnSave;
    private Integer eventType;


    
    

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        programParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Program) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
        //initView(eventType, "sp.crud.country");
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
           loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtName.setRawValue(null);
        txtdescription.setRawValue(null);
        dtbcontdate.setRawValue(null);
        website.setRawValue(null);
        //Faltan los radio buton
        txtother.setRawValue(null);
        txtOtherNetWork.setRawValue(null);
        txtbin.setRawValue(null);
        txtotheBINN.setRawValue(null);
        dtbexpectedLaunchDate.setRawValue(null);
//Cambio prueba
    }

    private void loadFields(Program program) {
        try {
            txtName.setText(program.getName());
            txtdescription.setText(program.getDescription());
            dtbcontdate.setValue(program.getContractDate());
            website.setText(program.getWebSite());
            txtother.setText(program.getOtherSourceFunds());
           txtOtherNetWork.setText(program.getOtherResponsibleNetworkReporting());
           txtbin.setText(program.getBiniinNumber());
            dtbexpectedLaunchDate.setValue(program.getExpectedLaunchDate());

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtName.setReadonly(true);
        txtdescription.setReadonly(true);
        dtbcontdate.setReadonly(true);
        website.setReadonly(true);
        txtother.setReadonly(true);
        txtOtherNetWork.setReadonly(true);
        txtbin.setReadonly(true);
        dtbexpectedLaunchDate.setReadonly(true);
        
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtdescription.getText().isEmpty()) {
            txtdescription.setFocus(true);
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

    private void saveProgram( Program _program  )  {
        try {
            Program program = null;

            if (_program!= null) {
                program = _program;
            } else {//New country
                program = new Program();
            }
            /*
            private Textbox txtName;
    private Textbox txtdescription;
    private Textbox txtcontdate;
             */
            program.setName(txtName.getText());
            program.setDescription(txtdescription.getText());
            program.setContractDate(dtbcontdate.getValue());
            program.setProgramTypeId((ProgramType) cmbProgramType.getSelectedItem().getValue());
            program.setProductTypeId((ProductType) cmbProductType.getSelectedItem().getValue());
            program.setIssuerId((Issuer) cmbIssuer.getSelectedItem().getValue());
            program.setBinSponsorId((BinSponsor) cmbBinSponsor.getSelectedItem().getValue());
            //cmbCardType
           // program.((CardType) cmbCardType.getSelectedItem().getValue());
           // program.
            
            
           program = programEJB.saveProgram(program);
            programParam = program;
            this.showMessage("sp.common.save.success", false, null);
        } catch (WrongValueException ex) {
            showError(ex);
        
    }   catch (GeneralException ex) {
            Logger.getLogger(AdminProgramController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RegisterNotFoundException ex) {
            Logger.getLogger(AdminProgramController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(AdminProgramController.class.getName()).log(Level.SEVERE, null, ex);
        }

        
         } 
        
        
       
    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveProgram(null);
                    break;
                case WebConstants.EVENT_EDIT:
                     saveProgram(programParam);
                    break;
                default:
                    break;
            }
        }
    }

   

    
 
    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(programParam);
                loadcmbProgram(eventType);
                loadCmbCurrency(eventType);
                loadcmbProductType(eventType);
                loadcmbProductType(eventType);
                loadcmbIssuer(eventType);
                loadcmbProgramOwner(eventType);
                loadcmbBinSponsor(eventType);
                loadcmbCardType(eventType);
                loadcmbSourceOfFound(eventType);
                loadcmbNetWork(eventType);
                loadcmbCurrency(eventType);
                loadcmbresponsibleNetwoork(eventType);
                loadcmbcardIssuanceType(eventType);

                break;
            case WebConstants.EVENT_VIEW:
                loadFields(programParam);
                txtName.setDisabled(true);
                txtdescription.setDisabled(true);
                dtbcontdate.setDisabled(true);
                loadcmbProgram(eventType);
                loadCmbCurrency(eventType);
                loadcmbProgramType(eventType);
                loadcmbProductType(eventType);
                loadcmbIssuer(eventType);
                loadcmbProgramOwner(eventType);
                loadcmbBinSponsor(eventType);
                loadcmbCardType(eventType);
                loadcmbSourceOfFound(eventType);
                loadcmbNetWork(eventType);
                loadcmbCurrency(eventType);
                loadcmbresponsibleNetwoork(eventType);
                loadcmbcardIssuanceType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadcmbProgram(eventType);
                loadCmbCurrency(eventType);
                loadcmbProgramType(eventType);
                loadcmbProductType(eventType);
                loadcmbIssuer(eventType);
                loadcmbProgramOwner(eventType);
                loadcmbBinSponsor(eventType);
                loadcmbCardType(eventType);
                loadcmbSourceOfFound(eventType);
                loadcmbNetWork(eventType);
                loadcmbCurrency(eventType);
                loadcmbresponsibleNetwoork(eventType);
                loadcmbcardIssuanceType(eventType);

            default:
                break;
        }
    }

    private void loadcmbProgram(Integer evenInteger) {
   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    
    }

    private void loadcmbProductType(Integer evenInteger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadcmbIssuer(Integer evenInteger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadcmbProgramOwner(Integer evenInteger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadcmbBinSponsor(Integer evenInteger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadcmbCardType(Integer evenInteger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadcmbSourceOfFound(Integer evenInteger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadcmbNetWork(Integer eventType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadcmbCurrency(Integer eventType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadcmbresponsibleNetwoork(Integer eventType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadcmbcardIssuanceType(Integer eventType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadcmbProgramType(Integer evenInteger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadCmbCurrency(Integer evenInteger) {
         EJBRequest request1 = new EJBRequest();
        List<Currency> currencies;
 
        try {
            currencies = utilsEJB.getCurrency(request1);
            cmbCurrency.getItems().clear();
            for (Currency c : currencies) {
 
                Comboitem item = new Comboitem();
                item.setValue(c);
                item.setLabel(c.getSymbol());
                item.setDescription(c.getName());
                item.setParent(cmbCurrency);
                if (programParam != null && c.getId().equals(programParam.getCurrencyId().getId())) {
                    cmbCurrency.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_ADD)) {
                cmbCurrency.setSelectedIndex(1);
            } if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbCurrency.setDisabled(true);
            }
            
            
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
 
    }

        
        
        
    }

    //public void loadData() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    

   


