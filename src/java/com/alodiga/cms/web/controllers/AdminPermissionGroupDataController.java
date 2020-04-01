package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
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
import com.cms.commons.models.KindCard;
import com.cms.commons.models.Language;
import com.cms.commons.models.LevelProduct;
import com.cms.commons.models.PermissionGroup;
import com.cms.commons.models.PermissionGroupData;
import com.cms.commons.models.Product;
import com.cms.commons.models.ProductUse;
import com.cms.commons.models.Program;
import com.cms.commons.models.ProgramType;
import com.cms.commons.models.SegmentMarketing;
import com.cms.commons.models.StorageMedio;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminPermissionGroupDataController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private UtilsEJB utilsEJB = null;
    private PermissionGroupData permissionGroupDataParam;
    private Textbox txtDescription;
    private Combobox cmbPermiGroupId;
    private Combobox cmbLanguageId;
    private Label lblAliasPermiGroup;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        Sessions.getCurrent();
        permissionGroupDataParam = (Sessions.getCurrent().getAttribute("object") != null) ? (PermissionGroupData) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:   
                tbbTitle.setLabel(Labels.getLabel("cms.crud.permission.group.data.edit"));
                break;
            case WebConstants.EVENT_VIEW:  
                tbbTitle.setLabel(Labels.getLabel("cms.crud.permission.group.data.view"));
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
        txtDescription.setRawValue(null);
    }
    
    private void loadFields(PermissionGroupData permissionGroupData) {
        try {
            txtDescription.setText(permissionGroupData.getDescription());
        } catch (Exception ex) {
            showError(ex);
        }
        
    }

    public void blockFields() {
        txtDescription.setReadonly(true);
        cmbPermiGroupId.setReadonly(true);
        cmbLanguageId.setReadonly(true);
        lblAliasPermiGroup.setVisible(true);
        btnSave.setVisible(false);
    }

    private void savePermissionGroupData(PermissionGroupData _permissionGroupData) throws RegisterNotFoundException, NullParameterException, GeneralException {
        try {
            PermissionGroupData permissionGroupData = null;
            
            if (_permissionGroupData != null) {
                permissionGroupData = _permissionGroupData;
            } else {//New Product
                permissionGroupData = new PermissionGroupData();
            }

            //Guardar PermissionGroupData
            permissionGroupData.setPermissionGroupId((PermissionGroup) cmbPermiGroupId.getSelectedItem().getValue());
            permissionGroupData.setLanguageId((Language) cmbLanguageId.getSelectedItem().getValue());
            permissionGroupData.setAlias((lblAliasPermiGroup).getValue());
            permissionGroupData.setDescription(txtDescription.getText());
            permissionGroupData = utilsEJB.savePermissionGroupData(permissionGroupData);
            permissionGroupDataParam = permissionGroupData;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }
    }
            
        public Boolean validateEmpty() {
        if (txtDescription.getText().isEmpty()) {
            txtDescription.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    savePermissionGroupData(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    savePermissionGroupData(permissionGroupDataParam);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void onChange$cmbPermiGroupId() {
        cmbLanguageId.setVisible(true);
        PermissionGroup permissionGroup = (PermissionGroup) cmbPermiGroupId.getSelectedItem().getValue();
    }
    
    public void onChange$cmbLanguageId() {
        Language language = (Language) cmbLanguageId.getSelectedItem().getValue();
        lblAliasPermiGroup.setVisible(true);
    }
   
    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(permissionGroupDataParam);
                loadCmbPermiGroupId(eventType);
                loadCmbLanguageId(eventType);
                onChange$cmbPermiGroupId();
                onChange$cmbLanguageId();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(permissionGroupDataParam);
                txtDescription.setReadonly(true);
//                lblAliasPermiGroup.setReadonly(true);
                loadCmbPermiGroupId(eventType);
                loadCmbLanguageId(eventType);
                blockFields();
                onChange$cmbPermiGroupId();
                onChange$cmbLanguageId();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbPermiGroupId(eventType);
                loadCmbLanguageId(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbPermiGroupId(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<PermissionGroup> permissionGroup;
        try {
            permissionGroup = utilsEJB.getPermissionGroup(request1);
            loadGenericCombobox(permissionGroup,cmbPermiGroupId, "id",evenInteger,Long.valueOf(permissionGroupDataParam != null? permissionGroupDataParam.getPermissionGroupId().getId(): 0) );            
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
    
    private void loadCmbLanguageId(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<Language> languageList;
        try {
            languageList = utilsEJB.getLanguage(request1);
            loadGenericCombobox(languageList,cmbLanguageId,"id",eventType,Long.valueOf(permissionGroupDataParam != null? permissionGroupDataParam.getLanguageId().getId(): 0) );            
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
    
//    private void loadCmbAliasPermiGroup(Integer eventType) {
//        EJBRequest request1 = new EJBRequest();
//        List<PermissionGroupData> permissionGroupDataList;
//        try {
//            permissionGroupDataList = utilsEJB.getPermissionGroupData(request1);
////            loadGenericCombobox(permissionGroupDataList, cmbAliasPermiGroup,"name",eventType,Long.valueOf(permissionGroupDataParam != null? permissionGroupDataParam.getAlias().toString(): 0) );            
//        } catch (EmptyListException ex) {
//            showError(ex);
//            ex.printStackTrace();
//        } catch (GeneralException ex) {
//            showError(ex);
//            ex.printStackTrace();
//        } catch (NullParameterException ex) {
//            showError(ex);
//            ex.printStackTrace();
//        }    
//    }   



    private void setText(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
