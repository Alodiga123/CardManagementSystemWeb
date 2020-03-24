package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Card;
import com.cms.commons.models.CardStatus;
import com.cms.commons.models.Product;
import com.cms.commons.models.Program;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class AdminCardManangerControllers extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblCarHolder;
    private Label lblCardNumber;
    private Label lblProgram;
    private Label lblProduct;
    private Label lblStatus;
    private Label lblExpirationDate;
    private Label lblIssueDate;
    private UtilsEJB utilsEJB = null;
    private CardEJB cardEJB = null;
    private ProgramEJB programEJB = null;
    private ProductEJB productEJB = null;
    private Card cardParam;
    private Button btnSave;
    private Integer evenType2 = -1;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        cardParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Card) Sessions.getCurrent().getAttribute("object") : null;
        evenType2 = (Integer) (Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE));
        initialize();
        loadData();
//      initView(eventType, "sp.crud.requestType");
    }

//    @Override
//    public void initView(int eventType, String adminView) {
//        super.initView(eventType, "sp.crud.requestType");
//    }
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

//    public void onChange$cmbProgram() {
//        cmbProduct.setVisible(true);
//        Program program = (Program) cmbProgram.getSelectedItem().getValue();
//        loadCmbProduct(eventType, program.getId());
//    }
    public void clearFields() {
    }

    private void loadFields(Card card) {
        try {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            lblCarHolder.setValue(card.getCardHolder());
            lblProgram.setValue(card.getProgramId().getName());
            lblProduct.setValue(card.getProductId().getName());
            lblCardNumber.setValue(card.getCardNumber());
            lblExpirationDate.setValue(simpleDateFormat.format(card.getExpirationDate()));
            lblIssueDate.setValue(simpleDateFormat.format(card.getIssueDate()));
//            lblExpirationDate.setValue(card.getExpirationDate().toString());
//            lblIssueDate.setValue(card.getIssueDate().toString());
            lblStatus.setValue(card.getCardStatusId().getDescription());
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
//            card.setCardHolder(txtCarHolder.getText());
//            card = cardEJB.saveCard(card);
//            cardParam = card;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (evenType2) {
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
        switch (evenType2) {
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

//    private void loadCmbProgram(Integer evenInteger) {
//        //cmbProgram
//        EJBRequest request1 = new EJBRequest();
//        List<Program> programs;
//
//        try {
//            programs = programEJB.getProgram(request1);
//            loadGenericCombobox(programs, cmbProgram, "name", evenInteger, Long.valueOf(cardParam != null ? cardParam.getProgramId().getId() : 0));
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
//    private void loadCmbProduct(Integer evenInteger, long programId) {
//        EJBRequest request1 = new EJBRequest();
//        cmbProduct.getItems().clear();
//        Map params = new HashMap();
//        params.put(QueryConstants.PARAM_PROGRAM_ID, programId);
//        request1.setParams(params);
//        List<Product> products;
//        try {
//            products = productEJB.getProductByProgram(request1);
//            loadGenericCombobox(products, cmbProduct, "name", evenInteger, Long.valueOf(cardParam != null ? cardParam.getProductId().getId() : 0));
//        } catch (EmptyListException ex) {
//            showError(ex);
//        } catch (GeneralException ex) {
//            showError(ex);
//        } catch (NullParameterException ex) {
//            showError(ex);
//        }
//    }
//    
//    private void loadCmbStatus(Integer evenInteger) {
//        //cmbProgram
//        EJBRequest request1 = new EJBRequest();
//        List<CardStatus> cardStatus;
//
//        try {
//            cardStatus = utilsEJB.getCardStatus(request1);
//            loadGenericCombobox(cardStatus, cmbStatus, "description", evenInteger, Long.valueOf(cardParam != null ? cardParam.getCardStatusId().getId() : 0));
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
}
