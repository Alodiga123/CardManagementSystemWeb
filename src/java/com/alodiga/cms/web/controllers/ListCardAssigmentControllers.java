package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.AccountCard;
import com.cms.commons.models.AccountProperties;
import com.cms.commons.models.Card;
import com.cms.commons.models.CardNumberCredential;
import com.cms.commons.models.CardRequestNaturalPerson;
import com.cms.commons.models.CardStatus;
import com.cms.commons.models.Channel;
import com.cms.commons.models.Request;
import com.cms.commons.models.ReviewRequest;
import com.cms.commons.models.NaturalCustomer;
import com.cms.commons.models.StatusAccount;
import com.cms.commons.models.StatusRequest;
import com.cms.commons.models.Transaction;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public class ListCardAssigmentControllers extends GenericAbstractListController<Request> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtRequestNumber;
    private RequestEJB requestEJB = null;
    private CardEJB cardEJB = null;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB = null;
    private List<Request> requests = null;
    private Date expirationDateCard;
    public static int indAddRequestPerson;
    public static int indRequestOption = 1;
    private String applicantName = "";
    private ReviewRequest reviewRequestParam;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    public void startListener() {
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            //Evaluar Permisos
            permissionEdit = true;
            permissionAdd = true;
            permissionRead = true;
            Sessions.getCurrent().setAttribute(WebConstants.OPTION_MENU, indRequestOption);
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            cardEJB = (CardEJB) EJBServiceLocator.getInstance().get(EjbConstants.CARD_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            getData();
            loadList(requests);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void loadList(List<Request> list) {
        applicantName = "";
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (Request request : list) {
                    item = new Listitem();
                    item.setValue(request);
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    item.appendChild(new Listcell(request.getRequestNumber()));
                    item.appendChild(new Listcell(simpleDateFormat.format(request.getRequestDate())));
                    item.appendChild(new Listcell(request.getStatusRequestId().getDescription()));
                    if (request.getPersonId() != null) {
                        if (request.getIndPersonNaturalRequest() == true) {
                            item.appendChild(new Listcell(Labels.getLabel("cms.menu.tab.naturalPerson")));
                            StringBuilder applicantName = new StringBuilder(request.getPersonId().getApplicantNaturalPerson().getFirstNames());
                            applicantName.append(" ");
                            applicantName.append(request.getPersonId().getApplicantNaturalPerson().getLastNames());
                            item.appendChild(new Listcell(applicantName.toString()));
                        } else {
                            item.appendChild(new Listcell(Labels.getLabel("cms.menu.legalPerson.list")));
                            applicantName = request.getPersonId().getLegalPerson().getEnterpriseName();
                            item.appendChild(new Listcell(applicantName));
                        }
                    }
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.setParent(lbxRecords);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        requests = new ArrayList<Request>();
        try {
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.STATUS_REQUEST_KEY, Constants.STATUS_REQUEST_APPROVED);
            request1.setParams(params);
            requests = requestEJB.getRequestsByStatus(request1);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        }
    }

    private void showEmptyList() {
        Listitem item = new Listitem();
        item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.setParent(lbxRecords);
    }

    public void onClick$btnAssigment() throws InterruptedException {
        try {
            loadDataList(requests);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtRequestNumber.setText("");
    }

    @Override
    public List<Request> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Card createCard(ReviewRequest reviewRequest, CardNumberCredential cardNumber, Request request,
        CardStatus cardStatus) {
        Card card = new Card();
        
        try {                
            StringBuilder applicantName = new StringBuilder(request.getPersonId().getApplicantNaturalPerson().getFirstNames());
            applicantName.append(" ");
            applicantName.append(request.getPersonId().getApplicantNaturalPerson().getLastNames());
            String cardHolder = applicantName.substring(0, applicantName.indexOf(" "));
            cardHolder = cardHolder + " ";
            StringBuilder applicantLastName = new StringBuilder(request.getPersonId().getApplicantNaturalPerson().getLastNames());
            applicantLastName.append(" ");
            applicantLastName.append(request.getPersonId().getApplicantNaturalPerson().getLastNames());
            String apellido = applicantLastName.substring(0, applicantLastName.indexOf(" "));
            cardHolder = cardHolder + apellido;                       
            card.setCardNumber(cardNumber.getCardNumber());
            card.setProgramId(request.getProgramId());
            card.setProductId(reviewRequestParam.getProductId());
            card.setCardHolder(cardHolder);
            card.setIssueDate(new Timestamp(new Date().getTime()));
            card.setExpirationDate(expirationDateCard);
            card.setSecurityCodeCard(cardNumber.getSecurityCodeCard());
            card.setCardStatusId(cardStatus);
            card.setPersonCustomerId(request.getPersonCustomerId());
            card.setCreateDate(new Timestamp(new Date().getTime()));
        } catch (Exception ex) {
            showError(ex);
        }
        return card;
    }

    public Card createLegalCard(ReviewRequest reviewRequest, CardNumberCredential cardNumber, Request request, CardStatus cardStatus) {
        Card card = new Card();
        try {
            card.setCardNumber(cardNumber.getCardNumber());
            card.setProgramId(request.getProgramId());
            card.setProductId(reviewRequestParam.getProductId());
            card.setCardHolder(request.getPersonId().getLegalPerson().getEnterpriseName());
            card.setIssueDate(new Timestamp(new Date().getTime()));
            card.setExpirationDate(expirationDateCard);
            card.setSecurityCodeCard(cardNumber.getSecurityCodeCard());
            card.setCardStatusId(cardStatus);
            card.setPersonCustomerId(request.getPersonCustomerId());
            card.setCreateDate(new Timestamp(new Date().getTime()));
        } catch (Exception ex) {
            showError(ex);
        }
        return card;
    }

    @Override
    public void loadDataList(List<Request> list) {
        Card card = null;
        CardStatus cardStatus = null;
        List<CardNumberCredential> cardNumberCredentialList = null;
        List<ReviewRequest> reviewRequestList = null;
        List<NaturalCustomer> cardComplementaryList = null;
        List<CardRequestNaturalPerson> cardRequestList = null;
        CardNumberCredential cardNumber = null;
        Long countCardComplementary = 0L;       
        int i = 0;

        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {

                //Estatus de la tarjeta SOLICITADA
                EJBRequest request1 = new EJBRequest();
                request1.setParam(Constants.CARD_STATUS_REQUESTED);
                cardStatus = utilsEJB.loadCardStatus(request1);

                //se obtienen las tarjetas que este disponibles para asignar
                EJBRequest request2 = new EJBRequest();
                Map params = new HashMap();
                params.put(Constants.USE_KEY, Constants.USE_NUMBER_CARD);
                request2.setParams(params);
                cardNumberCredentialList = cardEJB.getCardNumberCredentialByUse(request2);
                cardNumber = cardNumberCredentialList.get(i);           

                //Se asignan las tarjetas a las solicitudes aprobadas
                for (Request r : list) {
                    //Se busca el producto por medio de ReviewRequest
                    EJBRequest request3 = new EJBRequest();
                    params = new HashMap();
                    params.put(QueryConstants.PARAM_REQUEST_ID, r.getId());
                    params.put(QueryConstants.PARAM_REVIEW_REQUEST_TYPE_ID, Constants.REVIEW_REQUEST_TYPE_COLLECTIONS);
                    request3.setParams(params);
                    reviewRequestList = requestEJB.getReviewRequestByRequest(request3);

                    for (ReviewRequest review : reviewRequestList) {
                        reviewRequestParam = review;
                    }

                    //Se calcula la fecha de vencimiento de la tarjeta
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Timestamp(new Date().getTime()));
                    cal.add(Calendar.MONTH, reviewRequestParam.getProductId().getValidityMonths());
                    expirationDateCard = cal.getTime();

                    //Caso Solicitante Natural
                    if (r.getPersonTypeId().getIndNaturalPerson() == true) {
                        //Asignar tarjeta al solicitante principal
                        cardNumber = cardNumberCredentialList.get(i);
                        card = createCard(reviewRequestParam, cardNumber, r, cardStatus);
                        card = saveCard(card);
                        updateCardNumberAssigned(cardNumber);
                        createAccount(card,r);
                        //Actualiza el estatus de la solicitud
                        updateStatusRequest(r);
                        i++;                        

                        //Se verifica si hay solicitantes complementarios
                        countCardComplementary = personEJB.countCardComplementaryByApplicant(r.getPersonId().getApplicantNaturalPerson().getId());
                        if (countCardComplementary > 0) {
                            //Obtiene Lista de solicitantes complementarios
                            EJBRequest request4 = new EJBRequest();
                            params = new HashMap();
                            params.put(Constants.NATURAL_CUSTOMER_KEY, r.getPersonCustomerId().getNaturalCustomer().getId());
                            request4.setParams(params);
                            cardComplementaryList = personEJB.getNaturalCustomerByCardComplementaries(request4);
                            
                            //Asignar tarjetas a solicitantes complementarios
                            if (cardComplementaryList != null) {
                                for (NaturalCustomer cardComplementaries : cardComplementaryList) {
                                    card = new Card();
                                    cardNumber = cardNumberCredentialList.get(i);                                
                                    StringBuilder applicantName = new StringBuilder(cardComplementaries.getFirstNames());
                                    applicantName.append(" ");
                                    applicantName.append(cardComplementaries.getLastNames());
                                    String cardHolder = applicantName.substring(0, applicantName.indexOf(" "));
                                    cardHolder = cardHolder + " ";
                                    StringBuilder applicantLastName = new StringBuilder(cardComplementaries.getLastNames());
                                    applicantLastName.append(" ");
                                    applicantLastName.append(cardComplementaries.getLastNames());
                                    String apellido = applicantLastName.substring(0, applicantLastName.indexOf(" "));
                                    cardHolder = cardHolder + apellido;
                                    card.setCardNumber(cardNumber.getCardNumber());
                                    card.setProgramId(r.getProgramId());
                                    card.setProductId(reviewRequestParam.getProductId());
                                    card.setCardHolder(cardHolder);
                                    card.setIssueDate(new Timestamp(new Date().getTime()));
                                    card.setExpirationDate(expirationDateCard);
                                    card.setSecurityCodeCard(cardNumber.getSecurityCodeCard());
                                    card.setCardStatusId(cardStatus);
                                    card.setPersonCustomerId(cardComplementaries.getPersonId());
                                    card.setCreateDate(new Timestamp(new Date().getTime()));
                                    card = saveCard(card);
                                    updateCardNumberAssigned(cardNumber);
                                    createAccount(card,r);
                                    i++;
                                }
                            }
                        }
                        
                    } else {
                        //Caso Solicitante Jurídico
                        //Obtiene lista de tarjetas adicionales (Empleados)
                        EJBRequest request5 = new EJBRequest();
                        params = new HashMap();
                        params.put(Constants.APPLICANT_LEGAL_PERSON_KEY, r.getPersonId().getLegalPerson().getId());
                        request5.setParams(params);
                        cardRequestList = personEJB.getCardRequestNaturalPersonsByLegalApplicant(request5);

                        //Asigna tarjeta a solicitante juridico
                        cardNumber = cardNumberCredentialList.get(i);
                        card = createLegalCard(reviewRequestParam, cardNumber, r, cardStatus);
                        card = saveCard(card);
                        updateCardNumberAssigned(cardNumber);
                        createAccount(card,r);
                        //Actualiza el estatus de la solicitud
                        updateStatusRequest(r);
                        i++;

                        if (cardRequestList != null) {
                            for (CardRequestNaturalPerson additionalCards : cardRequestList) {
                                card = new Card();
                                cardNumber = cardNumberCredentialList.get(i);
                                StringBuilder applicantName = new StringBuilder(additionalCards.getFirstNames());
                                applicantName.append(" ");
                                applicantName.append(additionalCards.getLastNames());
                                String cardHolder = applicantName.substring(0, applicantName.indexOf(" "));
                                cardHolder = cardHolder + " ";
                                StringBuilder applicantLastName = new StringBuilder(additionalCards.getLastNames());
                                applicantLastName.append(" ");
                                applicantLastName.append(additionalCards.getLastNames());
                                String apellido = applicantLastName.substring(0, applicantLastName.indexOf(" "));
                                cardHolder = cardHolder + apellido;
                                card.setCardNumber(cardNumber.getCardNumber());
                                card.setProgramId(r.getProgramId());
                                card.setProductId(reviewRequestParam.getProductId());
                                card.setCardHolder(cardHolder);
                                card.setIssueDate(new Timestamp(new Date().getTime()));
                                card.setExpirationDate(expirationDateCard);
                                card.setSecurityCodeCard(cardNumber.getSecurityCodeCard());
                                card.setCardStatusId(cardStatus);
                                card.setPersonCustomerId(r.getPersonCustomerId());
                                card.setCreateDate(new Timestamp(new Date().getTime()));
                                card = saveCard(card);
                                updateCardNumberAssigned(cardNumber);
                                createAccount(card,r);
                                i++;
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            showError(ex);
        }
        finally {
            if (cardNumberCredentialList == null) {
                this.showMessage("cms.msj.notCardNumbersAvailable", false, null);
            }
            if (i > 0) {
                this.showMessage("cms.common.msj.assignCard", false, null);
            }            
        }
    }
    
    public void updateStatusRequest(Request cardRequest) {
        try {
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.STATUS_REQUEST_CUSTOMER_ASSIGNED_CARD);
            StatusRequest statusRequest = requestEJB.loadStatusRequest(request1);
            cardRequest.setStatusRequestId(statusRequest);
            cardRequest = requestEJB.saveRequest(cardRequest);
        } catch (RegisterNotFoundException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex); 
        } catch (GeneralException ex) {
            showError(ex);
        }
        
    }
    
    public Card saveCard(Card card) {
        try {
            card = cardEJB.saveCard(card);
            card.setSequentialNumber(card.getId().intValue());
            card = cardEJB.saveCard(card);
        } catch (Exception ex) {
            showError(ex);
        } finally {
            this.showMessage("cms.msj.errorSaveCard", false, null);
        }
        return card;
    }

    public void createAccount(Card cardNumber, Request request) {
        AccountCard accountCard = new AccountCard();
        StatusAccount statusAccount = null;
        Transaction transactionAccount = null;
        Channel channelAccount = null;
        List<AccountProperties> accountPropertiesList = null;
        String numberAccount = "";
        int lenghtAccount = 0;
        int num1=100;
        int num2=999; 
        try {

            //Se obtiene el estatus de la cuenta SOLICITADA
            EJBRequest request6 = new EJBRequest();
            request6 = new EJBRequest();
            request6.setParam(Constants.ACCOUNT_STATUS_REQUESTED);
            statusAccount = cardEJB.loadStatusAccount(request6);

            //Se busca la cuenta matrix
            EJBRequest request7 = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_COUNTRY_ID, request.getCountryId().getId());
            params.put(QueryConstants.PARAM_PROGRAM_ID, request.getProgramId().getId());
            request7.setParams(params);
            accountPropertiesList = cardEJB.getAccountPropertiesByRequest(request7);

            //Se obtiene la transaccion CREACION DE CUENTA
            EJBRequest request8 = new EJBRequest();
            request8 = new EJBRequest();
            request8.setParam(Constants.TRANSACTION_CREATION_ACCOUNT);
            transactionAccount = productEJB.loadTransaction(request8);
            
            //Se obtiene el tipo de canal INTERNO CMS
            EJBRequest request9 = new EJBRequest();
            request9 = new EJBRequest();
            request9.setParam(Constants.CHANNEL_CREATION_ACCOUNT);
            channelAccount = productEJB.loadChannel(request9);

            //Se crea de la Cuenta y se le asigna a la tarjeta del cliente
            if (accountPropertiesList != null) {
                for (AccountProperties accountProperties : accountPropertiesList) {
                    accountCard.setAccountPropertiesId(accountProperties);
                    lenghtAccount = accountProperties.getLenghtAccount();
                    accountCard.setAccountNumber(createNumberAccount(lenghtAccount));
                    accountCard.setStatusAccountId(statusAccount);
                    accountCard.setCardId(cardNumber);
                    accountCard.setTransactionId(transactionAccount);
                    accountCard.setChannelId(channelAccount);
                    accountCard.setCreateDate(new Timestamp(new Date().getTime()));
                    accountCard = cardEJB.saveAccountCard(accountCard);
                }
            }
        } catch (EmptyListException ex) {
            Logger.getLogger(ListCardAssigmentControllers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(ListCardAssigmentControllers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(ListCardAssigmentControllers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RegisterNotFoundException ex) {
            Logger.getLogger(ListCardAssigmentControllers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String createNumberAccount(int lenghtAccount) {
        String numberAccount = "";
        int num1=0;
        int num2=0; 
        try {
            switch (lenghtAccount) {
                case WebConstants.CARD_LENGHT_12:
                    num1=100;
                    num2=999;
                    for (int i=0;i<4;i++){
                        int numAleatorio=(int)Math.floor(Math.random()*(num1-num2)+num2);
                        numberAccount = numberAccount+String.valueOf(numAleatorio);
                    }
                break;
                case WebConstants.CARD_LENGHT_16: 
                    num1=1001;
                    num2=9999;
                    for (int i=0;i<4;i++){
                        int numAleatorio=(int)Math.floor(Math.random()*(num1-num2)+num2);
                        numberAccount = numberAccount+String.valueOf(numAleatorio);
                    }
                break;    
                case WebConstants.CARD_LENGHT_20: 
                    num1=10001;
                    num2=99999;
                    for (int i=0;i<4;i++){
                        int numAleatorio=(int)Math.floor(Math.random()*(num1-num2)+num2);
                        numberAccount = numberAccount+String.valueOf(numAleatorio);
                    }
                break; 
            }
        } catch (Exception e) {
            showError(e);
        }
        return numberAccount;
    }
    
    public void updateCardNumberAssigned(CardNumberCredential cardNumber) {
        try {
            cardNumber.setIndUse(true);
            cardNumber.setUpdateDate(new Timestamp(new Date().getTime()));
            cardNumber = cardEJB.saveCardNumberCredential(cardNumber);
        } catch (Exception ex) {
            showError(ex);
        }
    }

}
