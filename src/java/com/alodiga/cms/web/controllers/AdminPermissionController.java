package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Permission;
import com.cms.commons.models.PermissionGroup;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class AdminPermissionController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private UtilsEJB utilsEJB = null;
    private Combobox cmbPermissionGroup;
    private Textbox txtAction;
    private Textbox txtEntity;
    private Textbox txtNamePermission;
    private Radio rEnabledYes;
    private Radio rEnabledNo;
    private Permission permissionParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;
        
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        Sessions.getCurrent();
        permissionParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Permission) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.permission.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.permission.view"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtAction.setRawValue(null);
        txtEntity.setRawValue(null);
        txtNamePermission.setRawValue(null);
    }
    
    private void loadFields(Permission permission) {
        try {
            txtAction.setText(permission.getName().toString());
            
            if (permission.getEnabled() == true) {
                rEnabledYes.setChecked(true);
            } else {
                rEnabledNo.setChecked(false);
            }
  
         } catch (Exception ex) {
            showError(ex);
        }    
       
    }

    public void blockFields() {
        txtAction.setReadonly(true);
        txtEntity.setReadonly(true);
        txtNamePermission.setReadonly(true);
        cmbPermissionGroup.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtAction.getText().isEmpty()) {
            txtAction.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtEntity.getText().isEmpty()) {
            txtEntity.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtEntity.getText().isEmpty()) {
            txtEntity.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtNamePermission.getText().isEmpty()) {
            txtNamePermission.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);    
            return true;
        }
        return false;

    }

    private void savePermission(Permission _permission) {
        Boolean indEnabled = true;
        try {
            Permission permission = null;
            if (_permission != null) {
               permission = _permission;
            } else {
                permission = new Permission();
            }
            
            if (rEnabledYes.isChecked()) {
                indEnabled = true;
            } else {
                indEnabled = false;
            }
            
            permission.setAction(txtAction.getText());
            permission.setEntity(txtEntity.getText());
            permission.setName(txtNamePermission.getText());
            permission.setEnabled(indEnabled);
            permission = utilsEJB.savePermission(permission);
            permissionParam = permission;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    savePermission(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    savePermission(permissionParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(permissionParam);
                loadCmbPermissionGroup(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(permissionParam);
                txtAction.setReadonly(true);
                txtEntity.setReadonly(true);
                txtNamePermission.setReadonly(true);
                blockFields();
                loadCmbPermissionGroup(eventType);
                rEnabledYes.setDisabled(true);
                rEnabledNo.setDisabled(true);
                break;
            case WebConstants.EVENT_ADD:
                loadFields(permissionParam);
                loadCmbPermissionGroup(eventType);
                break;
            default:
                break;
        }
    }
    
    private void loadCmbPermissionGroup(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<PermissionGroup> permissionGroup;
        try {
            permissionGroup = utilsEJB.getPermissionGroup(request1);
            loadGenericCombobox(permissionGroup, cmbPermissionGroup, "name", eventType, Long.valueOf(permissionParam != null ? permissionParam.getPermissionGroupId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
}
