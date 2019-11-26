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
import com.cms.commons.models.CardIssuanceType;
import com.cms.commons.models.CardType;
import com.cms.commons.models.NaturalPerson;
import com.cms.commons.models.Network;
import com.cms.commons.models.Person;
import com.cms.commons.models.Product;
import com.cms.commons.models.ProductType;
import com.cms.commons.models.Program;
import com.cms.commons.models.ProgramHasNetwork;

import com.cms.commons.models.ProgramType;
import com.cms.commons.models.ResponsibleNetworkReporting;
import com.cms.commons.models.SourceFunds;
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
import com.alodiga.cms.web.utils.WebConstants;

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
    private Radiogroup cashAcces;
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
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
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

    public void onChange$cmbSourceOfFound() {
        System.out.println("valor de source of found" + cmbSourceOfFound.getSelectedItem().getValue());
        String sourceOfFoundsOther = WebConstants.PROGRAM_SOURCE_OF_FOUND_OTROS;
        if (cmbSourceOfFound.getSelectedItem().getValue().toString().equals(sourceOfFoundsOther)) {

            txtOther.setReadonly(false);
        } else {

            txtOther.setReadonly(true);
        }
    }

    private void saveProgram(Program _program) {
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
            program.setName(txtName.getText());
            program.setDescription(txtDescription.getText());
            program.setContractDate(dtbContdate.getValue());
            program.setProgramTypeId((ProgramType) cmbProgramType.getSelectedItem().getValue());
            program.setProductTypeId((ProductType) cmbProductType.getSelectedItem().getValue());
            program.setIssuerId((Issuer) cmbIssuer.getSelectedItem().getValue());
            program.setBinSponsorId((BinSponsor) cmbBinSponsor.getSelectedItem().getValue());
            String id = cmbProgramOwner.getSelectedItem().getParent().getId();
            program.setCardProgramManagerId(((NaturalPerson) cmbProgramOwner.getSelectedItem().getValue()).getPersonId());
            program.setProgramOwnerId(((NaturalPerson) cmbProgramOwner.getSelectedItem().getValue()).getPersonId());
            program.setWebSite(website.getText());
            if ((branded.getSelectedItem().getValue().equals(WebConstants.PROGRAM_BRANDED_YES)) || (branded.getSelectedItem().getValue().equals(WebConstants.PROGRAM_BRANDED_SI))) {
                indBranded = 1;
            } else {
                indBranded = 0;
            }
            program.setSharedBrand(indBranded);

            if ((reloadable.getSelectedItem().getValue().equals(WebConstants.PROGRAM_RELOADABLE_YES)) || (branded.getSelectedItem().getValue().equals(WebConstants.PROGRAM_RELOADABLE_SI))) {
                indReloadable = 1;
            } else {
                indReloadable = 0;
            }

            program.setSharedBrand(indReloadable);

            program.setSourceFundsId((SourceFunds) cmbSourceOfFound.getSelectedItem().getValue());

            if ((cashAcces.getSelectedItem().getValue().equals(WebConstants.PROGRAM_CASHACCES_YES)) || (branded.getSelectedItem().getValue().equals(WebConstants.PROGRAM_CASHACCES_SI))) {
                indCashAcces = 1;
            } else {
                indCashAcces = 0;
            }

            program.setSharedBrand(indCashAcces);

            if ((international.getSelectedItem().getValue().equals(WebConstants.PROGRAM_INTERNATIONAL_YES)) || (branded.getSelectedItem().getValue().equals(WebConstants.PROGRAM_INTERNATIONAL_SI))) {
                indInternational = 1;
            } else {
                indInternational = 0;
            }
            program.setSharedBrand(indInternational);
            program.setResponsibleNetworkReportingId((ResponsibleNetworkReporting) cmbResponsibleNetwoork.getSelectedItem().getValue());
            program.setCardIssuanceTypeId((CardIssuanceType) cmbCardIssuanceType.getSelectedItem().getValue());
            //program.getBiniinNumber(txtBin.getText());
            program.setCurrencyId((Currency) cmbCurrency.getSelectedItem().getValue());
            program = programEJB.saveProgram(program);

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

    private void loadCmbNetWork(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Network> networks;
        try {
            networks = utilsEJB.getNetworks(request1);
            loadGenericCombobox(networks, cmbNetWork, "name", evenInteger, Long.valueOf(0));
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
                item.setDescription(nameProgramOwner.toString());
                item.setParent(cmbProgramOwner);
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

    private void loadCmbCardType(Integer evenInteger) {
        //cmbCurrency
        EJBRequest request1 = new EJBRequest();
        List<CardType> cardTypes;

        try {
            cardTypes = utilsEJB.getCardTypes(request1);
            cmbCardType.getItems().clear();
            for (CardType c : cardTypes) {

                Comboitem item = new Comboitem();
                item.setValue(c);
                item.setLabel(c.getDescription());
                // item.setDescription(c.);
                item.setParent(cmbCardType);
                if (programParam != null && c.getId().equals(programParam.getCardIssuanceTypeId().getId())) {
                    cmbCardType.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_ADD)) {
                cmbProgramOwner.setSelectedIndex(1);
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
}
