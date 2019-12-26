package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Currency;
import com.cms.commons.models.Issuer;
import com.cms.commons.models.BinSponsor;
import com.cms.commons.models.CardIssuanceType;
import com.cms.commons.models.CardType;
import com.cms.commons.models.NaturalPerson;
import com.cms.commons.models.ProductType;
import com.cms.commons.models.Program;
import com.cms.commons.models.ProgramType;
import com.cms.commons.models.ResponsibleNetworkReporting;
import com.cms.commons.models.SourceFunds;
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
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.LegalPerson;
import java.sql.Timestamp;
import java.util.Date;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tab;

public class AdminProgramController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtName;
    private Textbox txtDescription;
    private Textbox txtBinIin;
    private Textbox txtOtherSourceOfFound;
    private Textbox txtOtheResponsibleNetwoork;
    private Textbox website;
    private Datebox dtbContrato;
    private Datebox dtbExpectedLaunchDate;
    private Combobox cmbProgramType;
    private Combobox cmbProductType;
    private Combobox cmbBinSponsor;
    private Combobox cmbIssuer;
    private Combobox cmbProgramOwner;
    private Combobox cmbCardProgramManager;
    private Combobox cmbCardType;
    private Combobox cmbSourceOfFound;
    private Combobox cmbCurrency;
    private Combobox cmbResponsibleNetwoork;
    private Combobox cmbCardIssuanceType;
    private Radio rBrandedYes;
    private Radio rBrandedNo;
    private Radio rReloadableYes;
    private Radio rReloadableNo;
    private Radio rCashAccesYes;
    private Radio rCashAccesNo;
    private Radio rInternationalYes;
    private Radio rInternationalNo;
    private ProgramEJB programEJB = null;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private Program programParam;
    private Tab tabNetwork;
    private Button btnSave;
    private Button btnAddNetWork;
    private Integer eventType;
    public static Program programParent = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        programParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Program) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtName.setRawValue(null);
        txtDescription.setRawValue(null);
        dtbContrato.setRawValue(null);
        txtOtherSourceOfFound.setRawValue(null);
        txtBinIin.setRawValue(null);
        txtOtheResponsibleNetwoork.setRawValue(null);
        dtbExpectedLaunchDate.setRawValue(null);
        website.setRawValue(null);
    }

    public Program getProgramParent() {
        return programParent;
    }

    private void loadFields(Program program) {
        try {
            txtName.setText(program.getName());
            txtDescription.setText(program.getDescription());
            dtbContrato.setValue(program.getContractDate());
            dtbExpectedLaunchDate.setValue(program.getExpectedLaunchDate());
            if (program.getReloadable() == 1) {
                rReloadableYes.setChecked(true);
            } else {
                rReloadableNo.setChecked(true);
            }
            txtOtherSourceOfFound.setText(program.getOtherSourceFunds());
            if (program.getSharedBrand() == 1) {
                rBrandedYes.setChecked(true);
            } else {
                rBrandedNo.setChecked(true);
            }
            website.setText(program.getWebSite());
            if (program.getCashAccess() == 1) {
                rCashAccesYes.setChecked(true);
            } else {
                rCashAccesNo.setChecked(true);
            }
            txtBinIin.setText(program.getBiniinNumber());
            if (program.getUseInternational() == 1) {
                rInternationalYes.setChecked(true);
            } else {
                rInternationalNo.setChecked(true);
            }
            txtOtheResponsibleNetwoork.setText(program.getOtherResponsibleNetworkReporting());
            programParent = program;
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtName.setReadonly(true);
        txtDescription.setReadonly(true);
        dtbContrato.setDisabled(true);
        txtOtherSourceOfFound.setReadonly(true);
        txtBinIin.setReadonly(true);
        txtOtheResponsibleNetwoork.setReadonly(true);
        dtbExpectedLaunchDate.setDisabled(true);
        website.setReadonly(true);
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

    public void onChange$cmbSourceOfFound() {
        String sourceOfFoundsOther = WebConstants.PROGRAM_SOURCE_OF_FOUND_OTROS;
        String sourceOfFoundsOthers = WebConstants.PROGRAM_SOURCE_OF_FOUND_OTHER;

        String cadena = (((SourceFunds) cmbSourceOfFound.getSelectedItem().getValue()).getDescription());

        if ((cadena.equals(sourceOfFoundsOther)) || (cadena.equals(sourceOfFoundsOthers))) {
            txtOtherSourceOfFound.setDisabled(false);
        } else {
            txtOtherSourceOfFound.setDisabled(true);
        }
    }

    public void onChange$cmbResponsibleNetwoork() {
        String responsibleNetwoorkOther = WebConstants.PROGRAM_SOURCE_OF_FOUND_OTROS;
        String responsibleNetwoorkOthers = WebConstants.PROGRAM_SOURCE_OF_FOUND_OTHER;

        String cadena = (((ResponsibleNetworkReporting) cmbResponsibleNetwoork.getSelectedItem().getValue()).getDescription());

        if ((cadena.equals(responsibleNetwoorkOther)) || (cadena.equals(responsibleNetwoorkOthers))) {
            txtOtheResponsibleNetwoork.setDisabled(false);
        } else {
            txtOtheResponsibleNetwoork.setDisabled(true);
        }
    }
    
    private void saveProgram(Program _program) {
        tabNetwork.setSelected(true);
        
        short indBranded = 0;
        short indReloadable = 0;
        short indCashAcces = 0;
        short indInternational = 0;
        try {
            Program program = null;

            if (_program != null) {
                program = _program;
            } else {
                program = new Program();
            }

            if (rBrandedYes.isChecked()) {
                indBranded = 1;
            } else {
                indBranded = 0;
            }

            if (rReloadableYes.isChecked()) {
                indReloadable = 1;
            } else {
                indReloadable = 0;
            }

            if (rCashAccesYes.isChecked()) {
                indCashAcces = 1;
            } else {
                indCashAcces = 0;
            }

            if (rInternationalYes.isChecked()) {
                indInternational = 1;
            } else {
                indInternational = 0;
            }

            program.setName(txtName.getText());
            program.setDescription(txtDescription.getText());
            program.setContractDate(dtbContrato.getValue());
            program.setProgramTypeId((ProgramType) cmbProgramType.getSelectedItem().getValue());
            program.setProductTypeId((ProductType) cmbProductType.getSelectedItem().getValue());
            program.setIssuerId((Issuer) cmbIssuer.getSelectedItem().getValue());
            program.setProgramOwnerId(((NaturalPerson) cmbProgramOwner.getSelectedItem().getValue()).getPersonId());
            program.setCardProgramManagerId(((LegalPerson) cmbCardProgramManager.getSelectedItem().getValue()).getPersonId());
            program.setBinSponsorId((BinSponsor) cmbBinSponsor.getSelectedItem().getValue());
            program.setExpectedLaunchDate(dtbExpectedLaunchDate.getValue());
            program.setCardIssuanceTypeId((CardIssuanceType) cmbCardIssuanceType.getSelectedItem().getValue());
            program.setReloadable(indReloadable);
            program.setSourceFundsId((SourceFunds) cmbSourceOfFound.getSelectedItem().getValue());
            if (!txtOtherSourceOfFound.getText().equals("")) {
                program.setOtherSourceFunds(txtOtherSourceOfFound.getText());
            }
            program.setSharedBrand(indBranded);
            program.setWebSite(website.getText());
            program.setCashAccess(indCashAcces);
            program.setBiniinNumber(txtBinIin.getText());
            program.setCurrencyId((Currency) cmbCurrency.getSelectedItem().getValue());
            program.setUseInternational(indInternational);
            program.setCreateDate(new Timestamp(new Date().getTime()));
            program.setResponsibleNetworkReportingId((ResponsibleNetworkReporting) cmbResponsibleNetwoork.getSelectedItem().getValue());
            if (!txtOtheResponsibleNetwoork.getText().equals("")) {
                program.setOtherResponsibleNetworkReporting(txtOtheResponsibleNetwoork.getText());
            }
            program = programEJB.saveProgram(program);
            programParent = program;
            
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

//    public void onClick$btnAddNetWork() {
//        try {
//            String view = "/adminAddNetwork.zul";
//            Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
//            Map<String, Object> paramsPass = new HashMap<String, Object>();
//            paramsPass.put("object", programParam);
//            final Window window = (Window) Executions.createComponents(view, null, paramsPass);
//            window.doModal();
//        } catch (Exception ex) {
//            this.showMessage("sp.error.general", true, ex);
//        }
//    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                txtOtherSourceOfFound.setDisabled(true);
                txtOtheResponsibleNetwoork.setDisabled(true);
                loadFields(programParam);
                loadCmbCurrency(eventType);
                loadCmbProductType(eventType);
                loadCmbProgramType(eventType);
                loadCmbIssuer(eventType);
                loadCmbProgramOwner(eventType);
                loadCmbCardProgramManager(eventType);
                loadCmbBinSponsor(eventType);
                loadCmbCardType(eventType);
                loadCmbSourceOfFound(eventType);
                loadCmbresponsibleNetwoork(eventType);
                loadCmbcardIssuanceType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(programParam);
                blockFields();
                loadCmbCurrency(eventType);
                loadCmbProgramType(eventType);
                loadCmbProductType(eventType);
                loadCmbIssuer(eventType);
                loadCmbProgramOwner(eventType);
                loadCmbCardProgramManager(eventType);
                loadCmbBinSponsor(eventType);
                loadCmbCardType(eventType);
                loadCmbSourceOfFound(eventType);
                loadCmbresponsibleNetwoork(eventType);
                loadCmbcardIssuanceType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                txtOtherSourceOfFound.setDisabled(true);
                txtOtheResponsibleNetwoork.setDisabled(true);
                loadCmbCurrency(eventType);
                loadCmbProgramType(eventType);
                loadCmbProductType(eventType);
                loadCmbIssuer(eventType);
                loadCmbProgramOwner(eventType);
                loadCmbCardProgramManager(eventType);
                loadCmbBinSponsor(eventType);
                loadCmbCardType(eventType);
                loadCmbSourceOfFound(eventType);
                loadCmbresponsibleNetwoork(eventType);
                loadCmbcardIssuanceType(eventType);
            default:
                break;
        }
    }

    private void loadCmbIssuer(Integer evenInteger) {
        //cmbIssuer
        EJBRequest request1 = new EJBRequest();
        List<Issuer> issuers;
        try {
            issuers = utilsEJB.getIssuers(request1);
            loadGenericCombobox(issuers, cmbIssuer, "name", evenInteger, Long.valueOf(programParam != null ? programParam.getIssuerId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbBinSponsor(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<BinSponsor> binSponsors;
        try {
            binSponsors = utilsEJB.getBinSponsor(request1);
            loadGenericCombobox(binSponsors, cmbBinSponsor, "description", evenInteger, Long.valueOf(programParam != null ? programParam.getBinSponsorId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbSourceOfFound(Integer evenInteger) {
        //CmbSourceOfFound
        EJBRequest request1 = new EJBRequest();
        List<SourceFunds> sourceFundses;
        try {
            sourceFundses = utilsEJB.getSourceFunds(request1);
            loadGenericCombobox(sourceFundses, cmbSourceOfFound, "description", evenInteger, Long.valueOf(programParam != null ? programParam.getSourceFundsId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbresponsibleNetwoork(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<ResponsibleNetworkReporting> responsibleNetworkReportings;
        try {
            responsibleNetworkReportings = utilsEJB.getResponsibleNetworkReportings(request1);
            loadGenericCombobox(responsibleNetworkReportings, cmbResponsibleNetwoork, "description", evenInteger, Long.valueOf(programParam != null ? programParam.getResponsibleNetworkReportingId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbcardIssuanceType(Integer evenInteger) {
        //cmbCardIssuanceType
        EJBRequest request1 = new EJBRequest();
        List<CardIssuanceType> cardIssuanceTypes;
        try {
            cardIssuanceTypes = utilsEJB.getCardIssuanceTypes(request1);
            loadGenericCombobox(cardIssuanceTypes, cmbCardIssuanceType, "description", evenInteger, Long.valueOf(programParam != null ? programParam.getCardIssuanceTypeId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }

    }

    private void loadCmbCurrency(Integer evenInteger) {
        //cmbCurrency
        EJBRequest request1 = new EJBRequest();
        List<Currency> currencies;
        try {
            currencies = utilsEJB.getCurrency(request1);
            loadGenericCombobox(currencies, cmbCurrency, "name", evenInteger, Long.valueOf(programParam != null ? programParam.getCurrencyId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbProgramType(Integer evenInteger) {
        //cmbProgramType
        EJBRequest request1 = new EJBRequest();
        List<ProgramType> programType;
        try {
            programType = utilsEJB.getProgramType(request1);
            loadGenericCombobox(programType, cmbProgramType, "name", evenInteger, Long.valueOf(programParam != null ? programParam.getProductTypeId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbProductType(Integer evenInteger) {
        //CmbProductType
        EJBRequest request1 = new EJBRequest();
        List<ProductType> productType;
        try {
            productType = utilsEJB.getProductTypes(request1);
            loadGenericCombobox(productType, cmbProductType, "name", evenInteger, Long.valueOf(programParam != null ? programParam.getProductTypeId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbCardType(Integer evenInteger) {
        //cmbCardType
        EJBRequest request1 = new EJBRequest();
        List<CardType> cardTypes;
        try {
            cardTypes = utilsEJB.getCardTypes(request1);
            loadGenericCombobox(cardTypes, cmbCardType, "description", evenInteger, Long.valueOf(programParam != null ? programParam.getCurrencyId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbProgramOwner(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<NaturalPerson> naturalPersons;
        try {
            naturalPersons = (List<NaturalPerson>) programEJB.getProgramOwner(request1);
            cmbProgramOwner.getItems().clear();
            for (NaturalPerson c : naturalPersons) {
                Comboitem item = new Comboitem();
                item.setValue(c);
                StringBuilder nameProgramOwner = new StringBuilder(c.getFirstNames());
                nameProgramOwner.append(" ");
                nameProgramOwner.append(c.getLastNames());
                item.setLabel(nameProgramOwner.toString());
                item.setDescription(c.getIdentificationNumber());
                item.setParent(cmbProgramOwner);
                //if (programParam != null && c.getId().equals(programParam.getProgramOwnerId().getNaturalPerson().getId())) {
                if (programParam != null && c.getId().equals(programParam.getProgramOwnerId().getId())) {
                    cmbProgramOwner.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbProgramOwner.setDisabled(true);
            }
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

    private void loadCmbCardProgramManager(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<LegalPerson> legalPersons;
        try {
            legalPersons = (List<LegalPerson>) programEJB.getCardManagementProgram(request1);
            cmbCardProgramManager.getItems().clear();
            for (LegalPerson c : legalPersons) {
                Comboitem item = new Comboitem();
                item.setValue(c);
                StringBuilder nameCardProgramManager = new StringBuilder(c.getEnterpriseName());
                nameCardProgramManager.append(" ");
                nameCardProgramManager.append(c.getTradeName());
                item.setLabel(nameCardProgramManager.toString());
                item.setDescription(c.getIdentificationNumber());
                item.setParent(cmbCardProgramManager);
                if (programParam != null && c.getId().equals(programParam.getCardProgramManagerId().getLegalPerson().getId())) {
                    cmbCardProgramManager.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbCardProgramManager.setDisabled(true);
            }
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
