package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Issuer;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.Profile;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static org.apache.commons.codec.digest.DigestUtils.md5;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminProfileController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtName;
    private Radio rEnabledYes;
    private Radio rEnabledNo;
    private UtilsEJB utilsEJB = null;
    private Profile profileParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
           profileParam = null;                    
       } else {
           profileParam = (Profile) Sessions.getCurrent().getAttribute("object");            
       }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.profile.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.profile.view"));
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
        txtName.setRawValue(null);
    } 
            
    private void loadFields(Profile profile) {
        try {
            txtName.setText(profile.getName());
            if (profile.getEnabled() == true) {
                rEnabledYes.setChecked(true);
            } else {
                rEnabledNo.setChecked(true);
            }
        
        } catch (Exception ex) {
            showError(ex);
        }
    }     

    public void blockFields() {
        txtName.setReadonly(true);
        btnSave.setVisible(false);
    }
    
    public Boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    private void saveProfile(Profile _profile) throws RegisterNotFoundException, NullParameterException, GeneralException {
        boolean indEnabled = true;
        try {
            Profile profile = null;

            if (_profile != null) {
                profile = _profile;
            } else {
                profile = new Profile();
            }

            if (rEnabledYes.isChecked()) {
                indEnabled = true;
            } else {
                indEnabled = false;
            }
            
            //Guarda el Perfil de Sistema
            profile.setName(txtName.getText());
            profile.setEnabled(indEnabled);
            profile = utilsEJB.saveProfile(profile);
            profileParam = profile;
            this.showMessage("sp.common.save.success", false, null);
        } catch (WrongValueException ex) {
            showError(ex);
        }
    }  
    
    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveProfile(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveProfile(profileParam);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void onclick$btnBack() {
        Executions.getCurrent().sendRedirect("listProfile.zul");
    }
    
    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(profileParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(profileParam);
                blockFields();
                txtName.setReadonly(true);
                rEnabledYes.setDisabled(true);
                rEnabledNo.setDisabled(true);
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    
    }    
    
    private void setText(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
  }