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
//import cmscredentialservicesclient.CMSCredentialServicesClient;
//import com.alodiga.cms.json.card.AssignVirtualCardResponse;
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
import com.cms.commons.models.PhonePerson;
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
    private Date cardAutomaticRenewalDate;
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
            loadDataList(requests);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    @Override
    public void loadDataList(List<Request> list) {
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
                            item.appendChild(new Listcell(Labels.getLabel("cms.common.legalPerson")));
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
            assignCard(requests);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public Card createCard(ReviewRequest reviewRequest, String cardNumber, Request request, CardStatus cardStatus, String accountAssigned) {
        Card card = new Card();
        boolean indRenewal = true;

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
            card.setAlias(cardNumber);
            card.setAssignedAccount(accountAssigned);
            card.setProgramId(request.getProgramId());
            card.setProductId(reviewRequestParam.getProductId());
            card.setCardHolder(cardHolder);
            card.setIssueDate(new Timestamp(new Date().getTime()));
            card.setExpirationDate(expirationDateCard);
            card.setCardStatusId(cardStatus);
            card.setPersonCustomerId(request.getPersonCustomerId());
            card.setCreateDate(new Timestamp(new Date().getTime()));
            card.setAutomaticRenewalDate(cardAutomaticRenewalDate);
            card.setIndRenewal(indRenewal);
        } catch (Exception ex) {
            showError(ex);
        }
        return card;
    }

    public Card createLegalCard(ReviewRequest reviewRequest, String cardNumber, Request request, CardStatus cardStatus) {
        Card card = new Card();
        try {
            card.setCardNumber(cardNumber);
            card.setProgramId(request.getProgramId());
            card.setProductId(reviewRequestParam.getProductId());
            card.setCardHolder(request.getPersonId().getLegalPerson().getEnterpriseName());
            card.setIssueDate(new Timestamp(new Date().getTime()));
            card.setExpirationDate(expirationDateCard);
            card.setCardStatusId(cardStatus);
            card.setPersonCustomerId(request.getPersonCustomerId());
            card.setCreateDate(new Timestamp(new Date().getTime()));
        } catch (Exception ex) {
            showError(ex);
        }
        return card;
    }

    public void assignCard(List<Request> list) {
        Card card = null;
        boolean indRenewal = true;
        CardStatus cardStatus = null;
        List<CardNumberCredential> cardNumberCredentialList = null;
        List<ReviewRequest> reviewRequestList = null;
        List<NaturalCustomer> cardComplementaryList = null;
        List<CardRequestNaturalPerson> cardRequestList = null;
        List<PhonePerson> phonePersonList = null;
        String cardNumber = null;
        String accountAssigned = null;
        Long countCardComplementary = 0L;  
        int i = 0;
        String movilPhone = "";
        
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                
                //Se instancia el WebService de Credencial para alta de las tarjetas
//                CMSCredentialServicesClient credentialWebService = new CMSCredentialServicesClient();
//                
                //Estatus de la tarjeta SOLICITADA
                EJBRequest request1 = new EJBRequest();
                request1.setParam(Constants.CARD_STATUS_REQUESTED);
                cardStatus = utilsEJB.loadCardStatus(request1);        

                //Se asignan las tarjetas virtuales y físicas a las solicitudes aprobadas
                for (Request r : list) {
                    //Se busca el producto por medio de ReviewRequest
                    EJBRequest request3 = new EJBRequest();
                    Map params = new HashMap();
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

                    //Se calcula la fecha para la renovacion automatica
                    cal.setTime(expirationDateCard);
                    cal.add(Calendar.DATE, -reviewRequestParam.getProductId().getDaysBeforeExpiration());
                    cardAutomaticRenewalDate = cal.getTime();

                    //Caso Solicitante Natural
                    if (r.getPersonTypeId().getIndNaturalPerson() == true) {
                        //Asignar tarjeta al solicitante principal
                        String initialsDocumentType = r.getPersonId().getApplicantNaturalPerson().getDocumentsPersonTypeId().getCodeIdentificationNumber();
                        String countryCode = r.getCountryId().getCodeIso3();
                        String identificationNumber = r.getPersonId().getApplicantNaturalPerson().getIdentificationNumber();
                        String pattern = "yyyyMMdd";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                        Date dateCard = new Date();
                        String highDateCard = simpleDateFormat.format(dateCard);
                        String dateBirthApplicant = simpleDateFormat.format(r.getPersonId().getApplicantNaturalPerson().getDateBirth());
                        String addressApplicant = r.getPersonId().getPersonHasAddress().getAddressId().getAddressLine1();
                        addressApplicant = addressApplicant.replace(" ", "%2B");
                        String stateCode = r.getPersonId().getPersonHasAddress().getAddressId().getCityId().getStateId().getCode();
                        String city = r.getPersonId().getPersonHasAddress().getAddressId().getCityId().getName();
                        city = city.replace(" ", "%2B");
                        String zipCode = r.getPersonId().getPersonHasAddress().getAddressId().getZipZoneCode();
                        //Buscar el teléfono móvil del solicitante
                        params = new HashMap();
                        params.put(Constants.PERSON_KEY, r.getPersonId().getId());
                        request1.setParams(params);
                        phonePersonList = personEJB.getPhoneByPerson(request1);
                        for (PhonePerson phone : phonePersonList) {
                            if (phone.getPhoneTypeId().getId() == Constants.PHONE_TYPE_MOBILE) {
                                movilPhone = phone.getNumberPhone();
                            }                                
                        }
                        if (movilPhone.length() == Constants.SIZE_NOT_VALID_NUMBER_PHONE) {
                            movilPhone.concat("0");
                        }
                        String email = r.getPersonId().getEmail();
                        String gender = r.getPersonId().getApplicantNaturalPerson().getGender();
                        String lastName = r.getPersonId().getApplicantNaturalPerson().getLastNames();
                        lastName = lastName.replace(" ", "%2B");
                        String firstName = r.getPersonId().getApplicantNaturalPerson().getFirstNames();
                        firstName = firstName.replace(" ", "%2B");
                        String taxInformationRegistry = r.getPersonId().getApplicantNaturalPerson().getTaxInformationRegistry();
                        String affinityCode = Constants.AFFINITY_CODE;
                        String recordingCard = Constants.NOT_RECORDING_CARD;
                        String annualLimitAmount = Float.toString(reviewRequestParam.getMaximumRechargeAmount()*12);
                        String cardDeliveryAddress = r.getPersonId().getPersonHasAddress().getAddressId().getAddressLine1();
                        cardDeliveryAddress = cardDeliveryAddress.replace(" ", "%2B");
                        String deliveryStateCode = r.getPersonId().getPersonHasAddress().getAddressId().getCityId().getStateId().getCode();
                        String deliverZipCode = r.getPersonId().getPersonHasAddress().getAddressId().getZipZoneCode();
//                        AssignVirtualCardResponse assignVirtualCardResponse = new AssignVirtualCardResponse();
//                        assignVirtualCardResponse = credentialWebService.assignVirtualCard(countryCode, initialsDocumentType, identificationNumber, highDateCard, dateBirthApplicant, addressApplicant, 
//                                                                                           stateCode, city, zipCode, movilPhone, email, gender, lastName, firstName, taxInformationRegistry, 
////                                                                                           affinityCode, recordingCard, cardDeliveryAddress, deliveryStateCode, city, deliverZipCode);
//                        cardNumber = assignVirtualCardResponse.getAlias();
//                        accountAssigned = assignVirtualCardResponse.getCtasig();
                        card = createCard(reviewRequestParam, cardNumber, r, cardStatus, accountAssigned);
                        card = saveCard(card);
                        createAccount(card,r,accountAssigned);
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

//                                    cardNumber = assignVirtualCardResponse.getAlias();
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

                                    card.setCardNumber(cardNumber);
                                    card.setProgramId(r.getProgramId());
                                    card.setProductId(reviewRequestParam.getProductId());
                                    card.setCardHolder(cardHolder);
                                    card.setIssueDate(new Timestamp(new Date().getTime()));
                                    card.setExpirationDate(expirationDateCard);
                                    card.setCardStatusId(cardStatus);
                                    card.setPersonCustomerId(cardComplementaries.getPersonId());
                                    card.setCreateDate(new Timestamp(new Date().getTime()));
                                    card.setAutomaticRenewalDate(cardAutomaticRenewalDate);
                                    card.setIndRenewal(indRenewal);
                                    card = saveCard(card);
                                    createAccount(card, r, accountAssigned);
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
//                        AssignVirtualCardResponse assignVirtualCardResponse = new AssignVirtualCardResponse();
//                        cardNumber = assignVirtualCardResponse.getAlias();;
//                        card = createLegalCard(reviewRequestParam, cardNumber, r, cardStatus);
//                        card = saveCard(card);
                        createAccount(card,r, accountAssigned);
                        //Actualiza el estatus de la solicitud
                        updateStatusRequest(r);
                        i++;

                        if (cardRequestList != null) {
                            for (CardRequestNaturalPerson additionalCards : cardRequestList) {
                                card = new Card();
//                                cardNumber = cardNumberCredentialList.get(i);
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
                                card.setCardNumber(cardNumber);
                                card.setProgramId(r.getProgramId());
                                card.setProductId(reviewRequestParam.getProductId());
                                card.setCardHolder(cardHolder);
                                card.setIssueDate(new Timestamp(new Date().getTime()));
                                card.setExpirationDate(expirationDateCard);
                                card.setCardStatusId(cardStatus);
                                card.setPersonCustomerId(r.getPersonCustomerId());
                                card.setCreateDate(new Timestamp(new Date().getTime()));
                                card.setAutomaticRenewalDate(cardAutomaticRenewalDate);
                                card.setIndRenewal(indRenewal);
                                card = saveCard(card);
                                createAccount(card, r, accountAssigned);
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

    public void createAccount(Card cardNumber, Request request, String accountAssigned) {
        AccountCard accountCard = new AccountCard();
        StatusAccount statusAccount = null;
        Transaction transactionAccount = null;
        Channel channelAccount = null;
        List<AccountProperties> accountPropertiesList = null;
        String numberAccount = "";
        int lenghtAccount = 0;
        int num1 = 100;
        int num2 = 999;
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
                    accountCard.setAccountNumber(accountAssigned);
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
        int num1 = 0;
        int num2 = 0;
        try {
            switch (lenghtAccount) {
                case WebConstants.CARD_LENGHT_12:
                    num1 = 100;
                    num2 = 999;
                    for (int i = 0; i < 4; i++) {
                        int numAleatorio = (int) Math.floor(Math.random() * (num1 - num2) + num2);
                        numberAccount = numberAccount + String.valueOf(numAleatorio);
                    }
                    break;
                case WebConstants.CARD_LENGHT_16:
                    num1 = 1001;
                    num2 = 9999;
                    for (int i = 0; i < 4; i++) {
                        int numAleatorio = (int) Math.floor(Math.random() * (num1 - num2) + num2);
                        numberAccount = numberAccount + String.valueOf(numAleatorio);
                    }
                    break;
                case WebConstants.CARD_LENGHT_20:
                    num1 = 10001;
                    num2 = 99999;
                    for (int i = 0; i < 4; i++) {
                        int numAleatorio = (int) Math.floor(Math.random() * (num1 - num2) + num2);
                        numberAccount = numberAccount + String.valueOf(numAleatorio);
                    }
                    break;
            }
        } catch (Exception e) {
            showError(e);
        }
        return numberAccount;
    }

      public void onClick$btnClear() throws InterruptedException {
      txtRequestNumber.setText("");

    }

    public List<Request> getFilterList(String filter) {
    List<Request> requestList_ = new ArrayList<Request>();
        try {
            if (filter != null && !filter.equals("")) {
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.STATUS_REQUEST_KEY, Constants.STATUS_REQUEST_APPROVED);
            params.put(Constants.PARAM_PERSON_NAME, filter);
            request1.setParams(params);
            requestList_ = requestEJB.searchRequestsByStatus(request1);
            } else {
                return requests;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return requestList_;    
    }

      public void onClick$btnSearch() throws InterruptedException {
        try {
            loadDataList(getFilterList(txtRequestNumber.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }
}
