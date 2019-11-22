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
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

public class AdminProgramController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtName;
    private Textbox txtDescription;
    private Datebox dtbContdate;
    private Combobox cmbProgramType;
    private Combobox cmbProductType;
    private Combobox cmbIssuer;
    private Combobox cmbProgramOwner;
    private Combobox cmbBinSponsor;
    private Combobox cmbCardType;
    private Textbox website;
    private Radiogroup branded;
    private Radiogroup reloadable;
    private Combobox cmbSourceOfFound;
    private Textbox txtOther;
    private Radiogroup CashAcces;
    private Radiogroup international;
    private Combobox cmbNetWork;
    private Textbox txtOtherNetWork;
    private Textbox txtBin;
    private Combobox cmbCurrency;
    private Combobox cmbResponsibleNetwoork;
    private Textbox txtOtheBINN;
    private Combobox cmbCardIssuanceType;
    private Datebox dtbExpectedLaunchDate;
    private ProgramEJB programEJB = null;
    private UtilsEJB utilsEJB = null;
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
        txtDescription.setRawValue(null);
        dtbContdate.setRawValue(null);
        website.setRawValue(null);
        //Faltan los radio buton
        txtOther.setRawValue(null);
        txtOtherNetWork.setRawValue(null);
        txtBin.setRawValue(null);
        txtOtheBINN.setRawValue(null);
        dtbExpectedLaunchDate.setRawValue(null);
//Cambio prueba
    }

    private void loadFields(Program program) {
        try {
            txtName.setText(program.getName());
            txtDescription.setText(program.getDescription());
            dtbContdate.setValue(program.getContractDate());
            website.setText(program.getWebSite());
            txtOther.setText(program.getOtherSourceFunds());
            txtOtherNetWork.setText(program.getOtherResponsibleNetworkReporting());
            txtBin.setText(program.getBiniinNumber());
            dtbExpectedLaunchDate.setValue(program.getExpectedLaunchDate());

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtName.setReadonly(true);
        txtDescription.setReadonly(true);
        dtbContdate.setReadonly(true);
        website.setReadonly(true);
        txtOther.setReadonly(true);
        txtOtherNetWork.setReadonly(true);
        txtBin.setReadonly(true);
        dtbExpectedLaunchDate.setReadonly(true);

        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtDescription.getText().isEmpty()) {
            txtDescription.setFocus(true);
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

    private void saveProgram(Program _program) {
        try {
            Program program = null;

            if (_program != null) {
                program = _program;
            } else {//New country
                program = new Program();
            }

            program.setName(txtName.getText());
            program.setDescription(txtDescription.getText());
            program.setContractDate(dtbContdate.getValue());
            program.setProgramTypeId((ProgramType) cmbProgramType.getSelectedItem().getValue());
            program.setProductTypeId((ProductType) cmbProductType.getSelectedItem().getValue());
            program.setIssuerId((Issuer) cmbIssuer.getSelectedItem().getValue());
            program.setBinSponsorId((BinSponsor) cmbBinSponsor.getSelectedItem().getValue());
            //facta laCardType
            program.setWebSite(website.getText());

            // program.((CardType) cmbCardType.getSelectedItem().getValue());
            // program.
            program = programEJB.saveProgram(program);
            programParam = program;
            this.showMessage("sp.common.save.success", false, null);
        } catch (WrongValueException ex) {
            showError(ex);

        } catch (GeneralException ex) {
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
                loadCmbCurrency(eventType);
                loadCmbProductType(eventType);
                loadCmbProgramType(eventType);
                loadCmbIssuer(eventType);
                loadCmbProgramOwner(eventType);
                loadCmbBinSponsor(eventType);
                loadCmbCardType(eventType);
                loadCmbSourceOfFound(eventType);
                loadCmbNetWork(eventType);
                loadCmbresponsibleNetwoork(eventType);
                loadCmbcardIssuanceType(eventType);

                break;
            case WebConstants.EVENT_VIEW:
                loadFields(programParam);
                txtName.setDisabled(true);
                txtDescription.setDisabled(true);
                dtbContdate.setDisabled(true);
                website.setDisabled(true);
                txtOther.setDisabled(true);
                txtOtherNetWork.setDisabled(true);
                txtBin.setDisabled(true);
                txtOtheBINN.setDisabled(true);
                //me faltan los radio grp
                loadCmbCurrency(eventType);
                loadCmbProgramType(eventType);
                loadCmbProductType(eventType);
                loadCmbIssuer(eventType);
                loadCmbProgramOwner(eventType);
                loadCmbBinSponsor(eventType);
                loadCmbCardType(eventType);
                loadCmbSourceOfFound(eventType);
                loadCmbNetWork(eventType);
                loadCmbresponsibleNetwoork(eventType);
                loadCmbcardIssuanceType(eventType);
                break;
            case WebConstants.EVENT_ADD:

                loadCmbCurrency(eventType);
                loadCmbProgramType(eventType);
                loadCmbProductType(eventType);
                loadCmbIssuer(eventType);
                loadCmbProgramOwner(eventType);
                loadCmbBinSponsor(eventType);
                loadCmbCardType(eventType);
                loadCmbSourceOfFound(eventType);
                loadCmbNetWork(eventType);
                loadCmbresponsibleNetwoork(eventType);
                loadCmbcardIssuanceType(eventType);

            default:
                break;
        }
    }

    private void loadCmbIssuer(Integer evenInteger) {

    }

    private void loadCmbBinSponsor(Integer evenInteger) {

    }

    private void loadCmbSourceOfFound(Integer evenInteger) {

    }

    private void loadCmbresponsibleNetwoork(Integer evenInteger) {

    }

    private void loadCmbcardIssuanceType(Integer evenInteger) {

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
            }
            if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
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

    private void loadCmbProgramType(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<ProgramType> programTypes;

        try {
            programTypes = utilsEJB.getProgramType(request);
            cmbProgramType.getItems().clear();
            for (ProgramType c : programTypes) {

                Comboitem item = new Comboitem();
                item.setValue(c);

                item.setDescription(c.getName());
                item.setParent(cmbCurrency);
                if (programParam != null && c.getId().equals(programParam.getProductTypeId().getId())) {
                    cmbProgramType.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_ADD)) {
                cmbProgramType.setSelectedIndex(1);
            }
            if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
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

    private void loadCmbNetWork(Integer evenInteger) {

    }

    private void loadCmbProductType(Integer evenInteger) {

    }

    private void loadCmbProgramOwner(Integer evenInteger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadCmbCardType(Integer evenInteger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}