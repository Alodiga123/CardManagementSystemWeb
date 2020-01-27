package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Program;
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
import com.cms.commons.models.DaysWeek;
import com.cms.commons.models.DaysWeekHasProgramLoyalty;
import com.cms.commons.models.Product;
import com.cms.commons.models.ProgramLoyalty;
import com.cms.commons.models.ProgramLoyaltyType;
import com.cms.commons.models.StatusProgramLoyalty;
import com.cms.commons.util.Constants;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;

public class AdminLoyaltyController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label txtStatus = null;
    private Textbox txtDescription;
    private Textbox txtObservations;
    private Textbox txtConversionRatePoints;
    private Datebox dtbStarDate;
    private Datebox dtbEndDate;
    private Combobox cmbProgram;
    private Combobox cmbProduct;
    private Combobox cmbProgramLoyaltyType;
    private Combobox cmbStatusProgramLoyalty;
    private Checkbox cbxMonday;
    private Checkbox cbxTuesday;
    private Checkbox cbxWednesday;
    private Checkbox cbxThursday;
    private Checkbox cbxFriday;
    private Checkbox cbxSaturday;
    private Checkbox cbxSunday;
    public Integer statusLoyalty = 1;
    private ProgramEJB programEJB = null;
    private ProductEJB productEJB = null;
    private ProgramLoyalty programLoyaltyParam;
    private List<DaysWeekHasProgramLoyalty> daysWeekHasProgramLoyaltyParam;
    private Button btnSave;
    private Integer eventType;
    private Tab tabParameters;
    private StatusProgramLoyalty statusPending;
    public static ProgramLoyalty programLoyaltyParent = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        programLoyaltyParam = (Sessions.getCurrent().getAttribute("object") != null) ? (ProgramLoyalty) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            EJBRequest request1 = new EJBRequest();
            if (eventType == WebConstants.EVENT_ADD) {
                request1 = new EJBRequest();
                request1.setParam(WebConstants.STATUS_PROGRAM_LOYALTY_PENDING);
                statusPending = programEJB.loadStatusProgramLoyalty(request1);
                txtStatus.setValue(statusPending.getDescription());
            } else {
                request1 = new EJBRequest();
                Map params = new HashMap();
                params.put(Constants.PROGRAM_LOYALTY_KEY, programLoyaltyParam.getId());
                request1.setParams(params);
                daysWeekHasProgramLoyaltyParam = programEJB.getDaysWeekHasProgramLoyaltyByLoyalty(request1);
            }
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtDescription.setRawValue(null);
        dtbStarDate.setRawValue(null);
        dtbEndDate.setRawValue(null);
        txtObservations.setRawValue(null);
        txtConversionRatePoints.setRawValue(null);
    }

    public ProgramLoyalty getProgramLoyaltyParent() {
        return programLoyaltyParent;
    }

    private void loadFields(ProgramLoyalty programLoyalty) {
        try {
            txtStatus.setValue(programLoyalty.getStatusProgramLoyaltyId().getDescription());
            txtDescription.setText(programLoyalty.getDescription());
            dtbStarDate.setValue(programLoyalty.getStartDate());
            dtbEndDate.setValue(programLoyalty.getEndDate());
            txtObservations.setText(programLoyalty.getObservations());
            txtConversionRatePoints.setText(programLoyalty.getConversionRatePoints().toString());
            programLoyaltyParent = programLoyalty;
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadFieldDays(List<DaysWeekHasProgramLoyalty> daysWeekHasProgramLoyalty) {
        try {
            for (DaysWeekHasProgramLoyalty d : daysWeekHasProgramLoyalty) {
                switch (Integer.parseInt(d.getDaysWeekId().getId().toString())) {
                    case 1:
                        cbxMonday.setChecked(true);
                        break;
                    case 2:
                        cbxTuesday.setChecked(true);
                        break;
                    case 3:
                        cbxWednesday.setChecked(true);
                        break;
                    case 4:
                        cbxThursday.setChecked(true);
                        break;
                    case 5:
                        cbxFriday.setChecked(true);
                        break;
                    case 6:
                        cbxSaturday.setChecked(true);
                        break;
                    case 7:
                        cbxSunday.setChecked(true);
                        break;
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtDescription.setReadonly(true);
        dtbStarDate.setReadonly(true);
        dtbEndDate.setReadonly(true);
        txtObservations.setReadonly(true);
        txtConversionRatePoints.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtDescription.getText().isEmpty()) {
            txtDescription.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtConversionRatePoints.getText().isEmpty()) {
            txtConversionRatePoints.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveProgramLoyalty(ProgramLoyalty _programLoyalty) {
        //tabParameters.setSelected(true);

        int indMonday = 0;
        int indTuesday = 0;
        int indWednesday = 0;
        int indThursday = 0;
        int indFriday = 0;
        int indSaturday = 0;
        int indSunday = 0;

        try {
            ProgramLoyalty programLoyalty = null;
            DaysWeekHasProgramLoyalty daysWeekHasProgramLoyalty = null;

            if (_programLoyalty != null) {
                programLoyalty = _programLoyalty;
            } else {
                programLoyalty = new ProgramLoyalty();
                daysWeekHasProgramLoyalty = new DaysWeekHasProgramLoyalty();
            }

            programLoyalty.setProgramId((Program) cmbProgram.getSelectedItem().getValue());
            programLoyalty.setProductId((Product) cmbProduct.getSelectedItem().getValue());
            programLoyalty.setDescription(txtDescription.getText());
            programLoyalty.setStartDate(dtbStarDate.getValue());
            programLoyalty.setEndDate(dtbEndDate.getValue());
            programLoyalty.setProgramLoyaltyTypeId((ProgramLoyaltyType) cmbProgramLoyaltyType.getSelectedItem().getValue());
            programLoyalty.setConversionRatePoints(Float.parseFloat(txtConversionRatePoints.getText()));
            if (eventType == WebConstants.EVENT_ADD) {
                programLoyalty.setStatusProgramLoyaltyId(statusPending);
            } else {
                programLoyalty.setStatusProgramLoyaltyId((StatusProgramLoyalty) cmbStatusProgramLoyalty.getSelectedItem().getValue());
            }
            programLoyalty.setObservations(txtObservations.getText());
            programLoyalty = programEJB.saveProgramLoyalty(programLoyalty);
            programLoyaltyParent = programLoyalty;

            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    if (cbxMonday.isChecked()) {
                        indMonday = 1;
                        saveDaysWeekHasProgramLoyalty(null, indMonday);
                    }
                    if (cbxTuesday.isChecked()) {
                        indTuesday = 2;
                        saveDaysWeekHasProgramLoyalty(null, 2);
                    }
                    if (cbxWednesday.isChecked()) {
                        indWednesday = 3;
                        saveDaysWeekHasProgramLoyalty(null, indWednesday);
                    }
                    if (cbxThursday.isChecked()) {
                        indThursday = 4;
                        saveDaysWeekHasProgramLoyalty(null, indThursday);
                    }
                    if (cbxFriday.isChecked()) {
                        indFriday = 5;
                        saveDaysWeekHasProgramLoyalty(null, indFriday);
                    }
                    if (cbxSaturday.isChecked()) {
                        indSaturday = 6;
                        saveDaysWeekHasProgramLoyalty(null, indSaturday);
                    }
                    if (cbxSunday.isChecked()) {
                        indSunday = 7;
                        saveDaysWeekHasProgramLoyalty(null, indSunday);
                    }
                    break;
                case WebConstants.EVENT_EDIT:
                    if (cbxMonday.isChecked()) {
                        indMonday = 1;
                        saveDaysWeekHasProgramLoyalty(daysWeekHasProgramLoyalty, 1);
                    }
                    if (cbxTuesday.isChecked()) {
                        indTuesday = 2;
                        saveDaysWeekHasProgramLoyalty(daysWeekHasProgramLoyalty, 2);
                    }
                    if (cbxWednesday.isChecked()) {
                        indWednesday = 3;
                        saveDaysWeekHasProgramLoyalty(daysWeekHasProgramLoyalty, 3);
                    }
                    if (cbxThursday.isChecked()) {
                        indThursday = 4;
                        saveDaysWeekHasProgramLoyalty(daysWeekHasProgramLoyalty, 4);
                    }
                    if (cbxFriday.isChecked()) {
                        indFriday = 5;
                        saveDaysWeekHasProgramLoyalty(daysWeekHasProgramLoyalty, 5);
                    }
                    if (cbxSaturday.isChecked()) {
                        indSaturday = 6;
                        saveDaysWeekHasProgramLoyalty(daysWeekHasProgramLoyalty, 6);
                    }
                    if (cbxSunday.isChecked()) {
                        indSunday = 7;
                        saveDaysWeekHasProgramLoyalty(daysWeekHasProgramLoyalty, 7);
                    }
                    break;
                default:
                    break;
            }
            this.showMessage("sp.common.save.success", false, null);

        } catch (WrongValueException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            Logger.getLogger(AdminLoyaltyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RegisterNotFoundException ex) {
            Logger.getLogger(AdminLoyaltyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(AdminLoyaltyController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveDaysWeekHasProgramLoyalty(DaysWeekHasProgramLoyalty _daysWeekHasProgramLoyalty, Integer indDay) {
        try {
            DaysWeekHasProgramLoyalty daysWeekHasProgramLoyalty = null;

            if (_daysWeekHasProgramLoyalty != null) {
                daysWeekHasProgramLoyalty = _daysWeekHasProgramLoyalty;
            } else {
                daysWeekHasProgramLoyalty = new DaysWeekHasProgramLoyalty();
            }
            
            EJBRequest request1 = new EJBRequest();
            request1.setParam(indDay);
            DaysWeek daysWeek = programEJB.loadDaysWeek(request1);   
            
            daysWeekHasProgramLoyalty.setProgramLoyaltyId(programLoyaltyParent);
            daysWeekHasProgramLoyalty.setDaysWeekId(daysWeek);
            daysWeekHasProgramLoyalty = programEJB.saveDaysWeekHasProgramLoyalty(daysWeekHasProgramLoyalty);
            
        } catch (WrongValueException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
            Logger.getLogger(AdminLoyaltyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(AdminLoyaltyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(AdminLoyaltyController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveProgramLoyalty(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveProgramLoyalty(programLoyaltyParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                txtStatus.setVisible(false);
                cmbStatusProgramLoyalty.setVisible(true);
                loadFields(programLoyaltyParam);
                loadFieldDays(daysWeekHasProgramLoyaltyParam);
                loadCmbProgram(eventType);
                loadCmbProduct(eventType);
                loadCmbProgramLoyaltyType(eventType);
                loadCmbStatusProgramLoyalty(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                txtStatus.setVisible(false);
                cmbStatusProgramLoyalty.setVisible(true);
                loadFields(programLoyaltyParam);
                loadFieldDays(daysWeekHasProgramLoyaltyParam);
                blockFields();
                loadCmbProgram(eventType);
                loadCmbProduct(eventType);
                loadCmbProgramLoyaltyType(eventType);
                loadCmbStatusProgramLoyalty(eventType);
                break;
            case WebConstants.EVENT_ADD:
                txtStatus.setVisible(true);
                cmbStatusProgramLoyalty.setVisible(false);
                loadCmbProgram(eventType);
                loadCmbProduct(eventType);
                loadCmbProgramLoyaltyType(eventType);
            default:
                break;
        }
    }

    private void loadCmbProgram(Integer evenInteger) {
        //cmbIssuer
        EJBRequest request1 = new EJBRequest();
        List<Program> programs;
        try {
            programs = programEJB.getProgram(request1);
            loadGenericCombobox(programs, cmbProgram, "name", evenInteger, Long.valueOf(programLoyaltyParam != null ? programLoyaltyParam.getProgramId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbProduct(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Product> products;
        try {
            products = productEJB.getProduct(request1);
            loadGenericCombobox(products, cmbProduct, "name", evenInteger, Long.valueOf(programLoyaltyParam != null ? programLoyaltyParam.getProductId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbProgramLoyaltyType(Integer evenInteger) {
        //cmbProgramLoyaltyType
        EJBRequest request1 = new EJBRequest();
        List<ProgramLoyaltyType> programLoyaltyTypes;
        try {
            programLoyaltyTypes = programEJB.getProgramLoyaltyType(request1);
            loadGenericCombobox(programLoyaltyTypes, cmbProgramLoyaltyType, "name", evenInteger, Long.valueOf(programLoyaltyParam != null ? programLoyaltyParam.getProgramLoyaltyTypeId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbStatusProgramLoyalty(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<StatusProgramLoyalty> statusProgramLoyaltys;
        try {
            statusProgramLoyaltys = (List<StatusProgramLoyalty>) programEJB.getStatusProgramLoyalty(request1);
            cmbStatusProgramLoyalty.getItems().clear();
            for (StatusProgramLoyalty c : statusProgramLoyaltys) {
                Comboitem item = new Comboitem();
                item.setValue(c);
                item.setLabel(c.getDescription());
                item.setDescription(c.getDescription());
                item.setParent(cmbStatusProgramLoyalty);
                if (programLoyaltyParam != null && c.getId().equals(programLoyaltyParam.getStatusProgramLoyaltyId().getId())) {
                    cmbStatusProgramLoyalty.setSelectedItem(item);
                }
            }
            if (eventType.equals(WebConstants.EVENT_ADD)) {
                cmbStatusProgramLoyalty.setSelectedIndex(0);
            }
            if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbStatusProgramLoyalty.setDisabled(true);
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