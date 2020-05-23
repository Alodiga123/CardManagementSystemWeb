package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.Card;
import com.cms.commons.models.CardDeliveryRegister;
import com.cms.commons.models.DeliveryRequest;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.sql.Timestamp;
import java.util.Date;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class AdminCardInventoryControllers extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label txtNumber;
    private Intbox txtCardNumberAttemps;
    private Textbox txtReceiverFirstName;
    private Textbox txtReceiverLastName;
    private Textbox txtObservations;
    private Datebox txtDaliveryDate;
    private Radio rDeliveryYes;
    private Radio rDeliveryNo;
    private UtilsEJB utilsEJB = null;
    private CardEJB cardEJB = null;
    private Card cardParam;
    public Window winAdminCardInventory;
    private Button btnSave;
    private Integer eventType;
    private AdminDeliveryRequestController adminDeliveryRequest = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        adminDeliveryRequest = new AdminDeliveryRequestController();
        eventType = (Integer) (Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE));
        if (adminDeliveryRequest.getDeliveryRequest() != null) {
            cardParam = (Card) Sessions.getCurrent().getAttribute("object");
//            eventType = adminDeliveryRequest.getEventType();
//        }
//        if (eventType == WebConstants.EVENT_ADD) {
//            cardParam = null;
        } else {
//            cardParam = (Card) Sessions.getCurrent().getAttribute("object");
            cardParam = null;
        }
        initialize();
        loadData();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            cardEJB = (CardEJB) EJBServiceLocator.getInstance().get(EjbConstants.CARD_EJB);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
    }

    public void onClick$btnBack() {
        winAdminCardInventory.detach();
    }

    private void loadDelivery(DeliveryRequest deliveryRequest) {
        try {
            txtNumber.setValue(deliveryRequest.getRequestNumber());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadFields(Card card) {
        try {

            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtCardNumberAttemps.setReadonly(true);
        txtDaliveryDate.setReadonly(true);
        txtReceiverFirstName.setReadonly(true);
        txtReceiverLastName.setReadonly(true);

        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtCardNumberAttemps.getText().isEmpty()) {
            txtCardNumberAttemps.setFocus(true);
            this.showMessage("cms.error.field.identificationNumber", true, null);
        } else if (txtDaliveryDate.getText().isEmpty()) {
            txtDaliveryDate.setFocus(true);
            this.showMessage("cms.error.field.fullName", true, null);
        } else if (txtReceiverFirstName.getText().isEmpty()) {
            txtReceiverFirstName.setFocus(true);
            this.showMessage("cms.error.field.fullName", true, null);
        } else if (txtReceiverLastName.getText().isEmpty()) {
            txtReceiverLastName.setFocus(true);
            this.showMessage("cms.error.field.fullName", true, null);
        } else if ((!rDeliveryYes.isChecked()) && (!rDeliveryNo.isChecked())) {
            rDeliveryYes.setFocus(true);
            this.showMessage("cms.error.field.delivery", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveCardStatus(Card _card) {
        try {
            Card card = null;

            if (_card != null) {
                card = _card;
            } else {//New requestType
                card = new Card();
            }

            if (rDeliveryYes.isChecked()) {
                //se actualiza el estatus de la tarjeta a INVENTARIO OK
            } else {
                //se actualiza el estatus de la tarjeta a INVENTARIO ERROR PERSONALIZACION
            }
            
            saveCardInvetory(card);

            this.showMessage("sp.common.save.success", false, null);
            btnSave.setVisible(false);
            EventQueues.lookup("updateCardInventory", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void saveCardInvetory(Card _card) {
        CardDeliveryRegister cardDeliveryRegister = new CardDeliveryRegister();
        boolean indDelivery;

        if (rDeliveryYes.isChecked()) {
            indDelivery = true;
        } else {
            indDelivery = false;
        }

        cardDeliveryRegister.setNumberDeliveryAttempts(txtCardNumberAttemps.intValue());
        cardDeliveryRegister.setDeliveryDate(txtDaliveryDate.getValue());
        cardDeliveryRegister.setReceiverFirstName(txtReceiverFirstName.getValue());
        cardDeliveryRegister.setReceiverLastName(txtReceiverLastName.getValue());
        cardDeliveryRegister.setDeliveryObservations(txtObservations.getValue());
        cardDeliveryRegister.setIndDelivery(indDelivery);
        if (eventType == 1) {
            cardDeliveryRegister.setCreateDate(new Timestamp(new Date().getTime()));
        } else {
            cardDeliveryRegister.setUpdateDate(new Timestamp(new Date().getTime()));
        }

        txtReceiverFirstName.setReadonly(true);
        txtReceiverLastName.setReadonly(true);
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveCardStatus(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveCardStatus(cardParam);
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(cardParam);
                loadDelivery(adminDeliveryRequest.getDeliveryRequest());
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(cardParam);
                loadDelivery(adminDeliveryRequest.getDeliveryRequest());
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
}
