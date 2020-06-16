package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Card;
import com.cms.commons.models.NewCardIssueRequest;
import com.cms.commons.models.PhonePerson;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

public class AdminRequestCardCanceledControllers extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblCardNumber;
    private Label lblNamesCardHolder;

    private Label lblRequestNumber;
    private Label lblStatusNewCardIssue;
    private Radio rConfirmationYes;
    private Radio rConfirmationNo;
    private Textbox txtObservations;
    private Datebox txtRequestDate;
    private CardEJB cardEJB = null;
    private PersonEJB personEJB = null;
    private Card cardCanceledParam;
    private NewCardIssueRequest newCardIssueRequestParam;
    private List<PhonePerson> phonePersonList = null;
    private List<NewCardIssueRequest> newCardIssueRequestList = null;
    private Button btnActivate;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) (Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE));
        if (eventType == WebConstants.EVENT_ADD) {
            cardCanceledParam = null;
        } else {
            cardCanceledParam = (Card) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
        loadData();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            cardEJB = (CardEJB) EJBServiceLocator.getInstance().get(EjbConstants.CARD_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);

            getNewCardIssueRequestParam();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public NewCardIssueRequest getNewCardIssueRequestParam() {
        try {
            
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_CARD_ID, cardCanceledParam.getId());
            request1.setParams(params);
            newCardIssueRequestList = cardEJB.getNewCardIssueRequestByCard(request);

            for (NewCardIssueRequest r : newCardIssueRequestList) {
                newCardIssueRequestParam = r;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return newCardIssueRequestParam;
    }

    public void clearFields() {
    }

    private void loadFields(Card cardCanceled) {
        try {
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();

            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            lblCardNumber.setValue(cardCanceled.getCardNumber());
            lblNamesCardHolder.setValue(cardCanceled.getCardHolder());

            btnActivate.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadFieldCanceled(NewCardIssueRequest newCardIssueRequest) {
        try {

            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            lblRequestNumber.setValue(newCardIssueRequest.getRequestNumber());
            lblStatusNewCardIssue.setValue(newCardIssueRequest.getStatusNewCardIssueRequestId().getDescription());
            if (txtObservations != null) {
                txtRequestDate.setValue(newCardIssueRequest.getRequestDate());
                txtObservations.setValue(newCardIssueRequest.getObservations());
            }

            if (newCardIssueRequest.getIndConfirmation() == true) {
                rConfirmationYes.setChecked(true);
            } else {
                rConfirmationNo.setChecked(true);
            }

            btnActivate.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtObservations.setReadonly(true);
        rConfirmationYes.setDisabled(true);
        rConfirmationNo.setDisabled(true);
        btnActivate.setVisible(false);
    }

    
    public Boolean validateEmpty() {
        if (txtObservations.getText().isEmpty()) {
            txtObservations.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtRequestDate.getText().isEmpty()) {
            txtRequestDate.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtObservations.getText().isEmpty()) {
            txtObservations.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    
    private void saveCardRenewal(NewCardIssueRequest _cardIssuerRequest) {
        boolean indConfirmation;
        try {
            NewCardIssueRequest cardIssuerRequest = null;

            if (_cardIssuerRequest != null) {
                cardIssuerRequest = _cardIssuerRequest;
            } else {//New country
                cardIssuerRequest = new NewCardIssueRequest();
            }
            
            if (rConfirmationYes.isChecked()) {
                indConfirmation = true;
            } else {
                indConfirmation = false;
            }

            cardIssuerRequest.setIndConfirmation(indConfirmation);
            cardIssuerRequest.setObservations(txtObservations.getText());
            cardIssuerRequest.setRequestDate(txtRequestDate.getValue());
            cardIssuerRequest = cardEJB.saveNewCardIssueRequest(cardIssuerRequest);

            this.showMessage("sp.common.save.success", false, null);

            btnActivate.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnActivate() {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveCardRenewal(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveCardRenewal(newCardIssueRequestParam);
                break;
            default:
                break;
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                getNewCardIssueRequestParam();
                if (newCardIssueRequestParam != null) {
                    loadFields(cardCanceledParam);
                    loadFieldCanceled(newCardIssueRequestParam);
                } else {
                    loadFields(cardCanceledParam);
                }
                break;
            case WebConstants.EVENT_VIEW:
                getNewCardIssueRequestParam();
                if (newCardIssueRequestParam != null) {
                    loadFields(cardCanceledParam);
                    loadFieldCanceled(newCardIssueRequestParam);
                } else {
                    loadFields(cardCanceledParam);
                }
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
}