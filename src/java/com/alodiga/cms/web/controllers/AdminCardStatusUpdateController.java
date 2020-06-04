package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.Card;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.text.SimpleDateFormat;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;

public class AdminCardStatusUpdateController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblCarHolder;
    private Label lblCardNumber;
    private Label lblTypeProduct;
    private Label lblIdentification;
    private Label lblStatus;
    private Label lblExpirationDate;
    private Label lblDateOfIssue;
    private Combobox cmbNewStatus;
    private Combobox cmbReasonChange;

    private Label lblUser;
    private Label lblCity;
    private Label lblIdentificationCardHolder;
    private Label lblComercial;

    

    

    

    
    private UtilsEJB utilsEJB = null;
    private CardEJB cardEJB = null;
    private ProgramEJB programEJB = null;
    private ProductEJB productEJB = null;
    private Card cardParam;
    private Button btnSave;
    private Integer evenType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        evenType = (Integer) (Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE));
        if (eventType == WebConstants.EVENT_ADD) {
            cardParam = null;
        } else {
            cardParam = (Card) Sessions.getCurrent().getAttribute("object");
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
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
    }

    private void loadFields(Card card) {
        try {
            
            User user = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            //lblIdentificationCardHolder.setValue(card.getCardHolder().);
            lblCarHolder.setValue(card.getCardHolder());
            lblTypeProduct.setValue(card.getProgramId().getProductTypeId().getName());
            //lblProduct.setValue(card.getProductId().getName());
            //lblCardNumber.setValue(card.getCardNumber());
            //lblExpirationDate.setValue(simpleDateFormat.format(card.getExpirationDate()));
            lblDateOfIssue.setValue(simpleDateFormat.format(card.getIssueDate()));
            lblComercial.setValue(user.getComercialAgencyId().getName());
            lblUser.setValue(user.getLogin());
            lblCity.setValue(user.getComercialAgencyId().getCityId().getName());
            lblIdentification.setValue(user.getIdentificationNumber());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        return true;
    }

    private void saveCardStatus(Card _card) {
        try {
            Card card = null;

            if (_card != null) {
                card = _card;
            } else {//New requestType
                card = new Card();
            }
            
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (evenType) {
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
        switch (evenType) {
            case WebConstants.EVENT_EDIT:
                loadFields(cardParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(cardParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }

}
