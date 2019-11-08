package com.alodiga.cms.web.generic.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import com.cms.commons.genericEJB.AbstractDistributionEntity;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Window;

public class GenericAbstractController extends GenericForwardComposer implements GenericDistributionController {

    public Integer eventType = null;
    public boolean permissionEdit;
    public boolean permissionAdd;
    public boolean permissionRead;
    public Long languageId = 1L;
    public Div divInfo;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        eventType = (Executions.getCurrent().getArg().get("eventType") != null) ? (Integer) Executions.getCurrent().getArg().get("eventType") : -1;
        super.doAfterCompose(comp);
    }

    @Override
    public void initialize() {
        
    }

    public void loadPermission(AbstractDistributionEntity clazz) throws Exception {
//        try {
//            permissionRead = AccessControl.hasPermission(clazz.getTableName(), PermissionConstants.EVENT_READ);
//            permissionEdit = AccessControl.hasPermission(clazz.getTableName(), PermissionConstants.EVENT_EDIT);
//            permissionAdd = AccessControl.hasPermission(clazz.getTableName(), PermissionConstants.EVENT_ADD);
//        } catch (TableNotFoundException ex) {
            //e.printStackTrace();
//            throw new Exception("error.general");
//        }
    }

    public void showMessage(String message, boolean isError, Exception exception) {
        divInfo.getChildren().clear();
        divInfo.setVisible(true);
        Hlayout hlayout = new Hlayout();
        Separator separator = new Separator();
        separator.setOrient("horizontal");
        separator.setParent(hlayout);
        Image icon = new Image();
        //icon.setSrc(isError? "/images/icon-cancel.png" : "/images/icon-enable.png");
        icon.setParent(hlayout);
        separator = new Separator();
        separator.setOrient("horizontal");
        separator.setParent(hlayout);
        Label lblMessage = new Label();
        lblMessage.setStyle("font-size:16px; font-weight: bold;font-style: italic;color: #424242;");
        lblMessage.setValue(Labels.getLabel(message) == null || Labels.getLabel(message).isEmpty() ? message : Labels.getLabel(message));
        lblMessage.setParent(hlayout);
        separator = new Separator();
        separator.setParent(divInfo);
        hlayout.setParent(divInfo);

        divInfo.setStyle(isError ? "background:#F4AFAF;border-radius:5px;" : "background:#B6E59E;border-radius:5px;");
        divInfo.setHeight("25px");
        if (exception != null) {
            exception.printStackTrace();
        }
    }
    
       public void showError(Exception exParam) {
        try {
            exParam.printStackTrace();
            String toPrint = exParam != null ? joinStackTrace(exParam.fillInStackTrace()): "";
            HashMap map = new HashMap<String, String>();
            map.put("message", toPrint);
            Window window = (Window) Executions.createComponents("error.zul", null, map);
            window.doModal();
        } catch (Exception ex) {
            Logger.getLogger(GenericAbstractController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
 
    }
    
     public static String joinStackTrace(Throwable e) {
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            //joinStackTrace(e, writer);
            return writer.toString();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                    // ignore
                }
            }
        }
    }
   
    public void clearMessage(){
        divInfo.setVisible(false);
        divInfo.getChildren().clear();
    }
}
