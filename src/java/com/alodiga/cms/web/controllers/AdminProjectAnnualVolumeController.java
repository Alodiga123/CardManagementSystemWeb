package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Program;
import com.cms.commons.models.ProjectAnnualVolume;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class AdminProjectAnnualVolumeController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Combobox cmbYear;
    private Textbox txtAccountsNumber;
    private Textbox txtActiveCardNumber;
    private Textbox txtAverageLoad;
    private Textbox txtAverageCardBalance;
    private ProgramEJB programEJB = null;
    private ProjectAnnualVolume projectAnnualVolumeParam;
    private Button btnSave;
    public Window winProjectAnnualVolume;
    private Integer eventType;
    private ProjectAnnualVolume projectAnnualVolume = null;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                projectAnnualVolumeParam = (ProjectAnnualVolume) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_VIEW:
                projectAnnualVolumeParam = (ProjectAnnualVolume) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_ADD:
                projectAnnualVolumeParam = null;
                break;
            default:
                break;
        }
        try {
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtAccountsNumber.setRawValue(null);
        txtActiveCardNumber.setRawValue(null);
        txtAverageLoad.setRawValue(null);
        txtAverageCardBalance.setRawValue(null);
    }
    
    private void loadFields(ProjectAnnualVolume projectAnnualVolume) {
    try {
            txtAccountsNumber.setValue(projectAnnualVolume.getAccountsNumber().toString());
            txtActiveCardNumber.setValue(projectAnnualVolume.getActiveCardNumber().toString());
            txtAverageLoad.setValue(projectAnnualVolume.getAverageLoad().toString());
            txtAverageCardBalance.setValue(projectAnnualVolume.getAverageCardBalance().toString());  
                 
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtAccountsNumber.setReadonly(true);
        txtActiveCardNumber.setReadonly(true);
        txtAverageLoad.setReadonly(true);
        txtAverageCardBalance.setReadonly(true);
        btnSave.setVisible(false);
        cmbYear.setDisabled(true);
    }
    
    public Boolean validateEmpty() {
        if (txtAccountsNumber.getText().isEmpty()) {
            txtAccountsNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtActiveCardNumber.getText().isEmpty()) {
            txtActiveCardNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtAverageLoad.getText().isEmpty()) {
            txtAverageLoad.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtAverageCardBalance.getText().isEmpty()) {
            txtAverageCardBalance.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveProjectAnnualVolume(ProjectAnnualVolume _projectAnnualVolume) throws RegisterNotFoundException, NullParameterException, GeneralException {
        Program program = null;
        List<ProjectAnnualVolume> projectAnnualVolumeList = null; 
        int indRegisterExist = 0;
        try {
            if (_projectAnnualVolume != null) {
                projectAnnualVolume = _projectAnnualVolume;
            } else {//New address
                projectAnnualVolume = new ProjectAnnualVolume();
            }
            
            //Program
            AdminProgramController adminProgram = new AdminProgramController();
            if (adminProgram.getProgramParent().getId() != null) {
                program = adminProgram.getProgramParent();
            }
            
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PROGRAM_KEY, program.getId());
            request1.setParams(params);
            projectAnnualVolumeList = programEJB.getProjectAnnualVolumeByProgram(request1);
            for (ProjectAnnualVolume p: projectAnnualVolumeList) {
                if (p.getYear() == Integer.parseInt(cmbYear.getSelectedItem().getValue().toString())) {
                    indRegisterExist = 1;
                    this.showMessage("sp.common.yearRegisterBD", false, null);
                }
            }     
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
           showError(ex); 
        } catch (GeneralException ex) {
            showError(ex);
        } finally {
            if (indRegisterExist != 1) {
                CreateProjectAnnualVolume(program,projectAnnualVolume);
                projectAnnualVolume = programEJB.saveProjectAnnualVolume(projectAnnualVolume);
                this.showMessage("sp.common.save.success", false, null);
                EventQueues.lookup("updateProjectAnnualVolume", EventQueues.APPLICATION, true).publish(new Event(""));
            }
        }
    }
    
    public ProjectAnnualVolume CreateProjectAnnualVolume(Program program, ProjectAnnualVolume projectAnnualVolume) {
        projectAnnualVolume.setProgramId(program);
        projectAnnualVolume.setYear(Integer.parseInt(cmbYear.getSelectedItem().getValue().toString()));
        projectAnnualVolume.setAccountsNumber(Integer.parseInt(txtAccountsNumber.getText()));
        projectAnnualVolume.setActiveCardNumber(Integer.parseInt(txtActiveCardNumber.getText()));
        projectAnnualVolume.setAverageLoad(Float.parseFloat(txtAverageLoad.getValue()));
        projectAnnualVolume.setAverageCardBalance(Float.parseFloat(txtAverageCardBalance.getValue()));
        return projectAnnualVolume;
    }

    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException {
        if (validateEmpty()) {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveProjectAnnualVolume(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveProjectAnnualVolume(projectAnnualVolumeParam);
                break;
            default:
                break;
        }
    }
    
    }    
    public void onClick$btnBack() {
        winProjectAnnualVolume.detach();
    }
    
    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(projectAnnualVolumeParam);
                loadCmbYear(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(projectAnnualVolumeParam);
                txtAccountsNumber.setDisabled(true);
                txtActiveCardNumber.setDisabled(true);
                txtAverageLoad.setDisabled(true);
                txtAverageCardBalance.setDisabled(true);
                loadCmbYear(eventType);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbYear(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbYear(Integer evenInteger) {
        ArrayList<Integer> yearProjection = new ArrayList<Integer>();
        for (int i = 1; i < 6; i++) {  
            yearProjection.add(Integer.valueOf(i));
        }
        try {
            Comboitem item = new Comboitem();
            for (Integer y: yearProjection) {
                item.setValue(y);
                item.setLabel("Year "+y);
                item.setParent(cmbYear);
                if (eventType != 1) {
                    if (y ==  Integer.valueOf(projectAnnualVolumeParam.getYear())) {
                        cmbYear.setSelectedItem(item);
                    }
                }
                item = new Comboitem(); 
            }
        } catch (Exception ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }
}
