package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Card;
import com.cms.commons.models.CardRenewalRequestHasCard;
import com.cms.commons.models.CardStatus;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.text.SimpleDateFormat;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class AdminCardRenewalRequestControllers extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblCardRequestRenewal;
    private Label lblCardNumber;
    private Label lblCardHolder;
    private Label lblCardProgram;
    private Label lblCardProduct;
    private Label lblCreateDate;
    private Label lblExpirationDate;
    private Combobox cmbCardStatus;
    private CardEJB cardEJB = null;
    private UtilsEJB utilsEJB = null;
    private CardRenewalRequestHasCard cardRenewalParam;
    public Window winAdminCardRenewalRequest;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) (Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE));
        if (eventType == WebConstants.EVENT_ADD) {
            cardRenewalParam = null;
        } else {
            cardRenewalParam = (CardRenewalRequestHasCard) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
        loadData();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            cardEJB = (CardEJB) EJBServiceLocator.getInstance().get(EjbConstants.CARD_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {

    }

    private void loadFields(CardRenewalRequestHasCard cardRenawal) {
        try {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            
            lblCardRequestRenewal.setValue(cardRenawal.getCardRenewalRequestId().getRequestNumber());
            lblCardNumber.setValue(cardRenawal.getCardId().getCardNumber());
            lblCardHolder.setValue(cardRenawal.getCardId().getCardHolder());
            lblCardProgram.setValue(cardRenawal.getCardId().getProgramId().getName());
            lblCardProduct.setValue(cardRenawal.getCardId().getProductId().getName());
            lblCreateDate.setValue(simpleDateFormat.format(cardRenawal.getCardId().getIssueDate()));
            lblExpirationDate.setValue(simpleDateFormat.format(cardRenawal.getCardId().getExpirationDate()));

            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    

    public void blockFields() {
        cmbCardStatus.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (cmbCardStatus.getSelectedItem() == null) {
            cmbCardStatus.setFocus(true);
            this.showMessage("cms.error.statusCard.notSelected", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveCard(Card _card) {
        try {
            CardRenewalRequestHasCard cardRenawalHasCard = null;
            Card card = null;

            if (_card != null) {
                card = _card;
            } else {//New card
                card = new Card();
            }

            card.setCardStatusId((CardStatus) cmbCardStatus.getSelectedItem().getValue());
            card = cardEJB.saveCard(card);

            this.showMessage("sp.common.save.success", false, null);
            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void onClick$btnBack() {
        winAdminCardRenewalRequest.detach();
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveCard(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveCard(cardRenewalParam.getCardId());
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(cardRenewalParam);
                loadCmbCardStatus(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(cardRenewalParam);
                loadCmbCardStatus(eventType);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCardStatus(eventType);
                break;
            default:
                break;
        }
    }
    
    private void loadCmbCardStatus(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<CardStatus> cardStatus;

        try {
            cardStatus = utilsEJB.getCardStatus(request1);
            loadGenericCombobox(cardStatus, cmbCardStatus, "description", evenInteger, Long.valueOf(cardRenewalParam != null ? cardRenewalParam.getCardId().getCardStatusId().getId() : 0));
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
