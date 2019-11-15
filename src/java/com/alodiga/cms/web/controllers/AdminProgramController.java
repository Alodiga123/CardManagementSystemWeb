package com.alodiga.cms.web.controllers;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Program;
import com.cms.commons.models.Country;
import com.cms.commons.models.Currency;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;

public class AdminProgramController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtName;
    private Textbox txtdescription;
    private Textbox txtcontdate;
    //private Textbox txtCodeIso3;
    private Combobox cmbProgramType;
    private Combobox cmbProductType;
    private Combobox cmbIssuer;
   private ProgramEJB programEJB = null;
    //private Combobox cmbCurrency;
    private Program programParam;
    private Button btnSave;
    private Integer eventType;
    

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        programParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Program) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute( WebConstants.EVENTYPE);
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
        txtcontdate.setRawValue(null);
        
//Cambio prueba
    }

    private void loadFields(Program program) {
        try {
            txtName.setText(program.getName());
            txtdescription.setText(program.getDescription());
            txtcontdate.setText(program.getContractDate().toString());
       //     txtCodeIso3.setText(country.getCodeIso3());

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtName.setReadonly(true);
        txtdescription.setReadonly(true);
        txtcontdate.setReadonly(true);
       // txtCodeIso3.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtdescription.getText().isEmpty()) {
            txtdescription.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        
        } else if (txtcontdate.getText().isEmpty()) {
            txtcontdate.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);    
            
            
            
        } else if (txtcontdate.getText().isEmpty()) {
            txtcontdate.setFocus(true);
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

    private void saveProgram(Program _Program) {
        try {
            Program program = null;
            

            if (_Program != null) {
                program = _Program;
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
            //program.setCodeIso2(txtCodeIso2.getText());
        //  program.setContractDate (txtcontdate.getText());
        //    program.setProgramTypeId((program) cmbProgramType.getSelectedItem().getValue());
            program = programEJB.saveProgram(program);
            programParam = program;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
           showError(ex);
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
                    loadcmbProductType(eventType);
                  //  loadcmbIssuer(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(programParam);
                txtName.setDisabled(true);
                
               
                txtdescription.setDisabled(true);
                txtcontdate.setDisabled(true);
                //txtCodeIso3.setDisabled(true);
                    loadcmbProgram(eventType);
                    loadcmbProductType(eventType);
              //      loadcmbIssuer(eventType);
                break;
            case WebConstants.EVENT_ADD:
                 loadcmbProgram(eventType);
                    loadcmbProductType(eventType);
                //    loadcmbIssuer(eventType);
                break;
            default:
                break;
        }
    }
    
    
   
    private void loadcmbProgram(Integer evenInteger) {
        //cmbCurrency
        EJBRequest request1 = new EJBRequest();
        List<Program> programies;
 
        try {
            programies = programEJB.getProgram(request1);
            cmbProgramType.getItems().clear();
            for (Program c : programies) {
 
                Comboitem item = new Comboitem();
                item.setValue(c);
                item.setLabel(c.getName());
                item.setDescription(c.getName());
                item.setParent(cmbProgramType);
                if (programParam != null && c.getId().equals(programParam.getProgramTypeId().getId())) {
                    cmbProgramType.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_ADD)) {
                cmbProgramType.setSelectedIndex(1);
            } if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbProgramType.setDisabled(true);
            }
            
            
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
 
    }

    
    // segundo aqui voy 
    
    private void loadcmbProductType(Integer evenInteger) {
        //cmbCurrency
        EJBRequest request1 = new EJBRequest();
        List<Program> programies;
 
        try {
            programies = programEJB.getProgram(request1);
            cmbProgramType.getItems().clear();
            for (Program c : programies) {
 
                Comboitem item = new Comboitem();
                item.setValue(c);
                item.setLabel(c.getName());
                item.setDescription(c.getName());
                item.setParent(cmbProgramType);
                if (programParam != null && c.getId().equals(programParam.getProgramTypeId().getId())) {
                    cmbProgramType.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_ADD)) {
                cmbProgramType.setSelectedIndex(1);
            } if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbProgramType.setDisabled(true);
            }
            
            
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
 
    }

    
    
   /* //tercero
private void loadcmbIssuer(Integer evenInteger) {
        //cmbCurrency
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
                if (countryParam != null && c.getId().equals(countryParam.getCurrencyId().getId())) {
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
*/


}






