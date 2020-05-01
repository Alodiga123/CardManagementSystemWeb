package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.LegalPerson;
import com.cms.commons.models.NaturalPerson;
import com.cms.commons.models.Person;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public class ListProgramOwnerController extends GenericAbstractListController<Person> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtRequestNumber;
    private PersonEJB personEJB = null;
    private List<Person> persons = null;
    private List<LegalPerson> legalPersonList = null;
    private List<NaturalPerson> naturalPersonList = null;
    public static int indOwnerOption = 2;

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
            Sessions.getCurrent().setAttribute(WebConstants.OPTION_MENU, indOwnerOption);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            getData();
            loadDataList(persons);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public int getIndOwnerOption() {
        return indOwnerOption;
    }
    
    public void onClick$btnAddProgramOwnerNaturalPerson() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Executions.getCurrent().sendRedirect("TabProgramOwnerNaturalPerson.zul");
    }

    public void onClick$btnAddProgramOwnerLegalPerson() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Executions.getCurrent().sendRedirect("TabProgramOwnerLegalPerson.zul");
    }

    public void onClick$btnDelete() {
    }

    public void loadDataList(List<Person> list) {
        String ownerNameLegal = "";
        NaturalPerson programOwnerNatural = null;
        LegalPerson programOwnerLegal = null;
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (Person person : list) {
                    item = new Listitem();
                    item.setValue(person);
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    item.appendChild(new Listcell(person.getCountryId().getName()));
                    item.appendChild(new Listcell(person.getPersonTypeId().getDescription()));
                    item.appendChild(new Listcell(simpleDateFormat.format(person.getCreateDate())));
                    if (person.getPersonTypeId().getIndNaturalPerson() == true) {
                        adminPage = "TabProgramOwnerNaturalPerson.zul";

                        StringBuilder OwnerName = new StringBuilder(person.getNaturalPerson().getFirstNames());
                        OwnerName.append(" ");
                        OwnerName.append(person.getNaturalPerson().getLastNames());
                        item.appendChild(new Listcell(OwnerName.toString()));

                        EJBRequest request1 = new EJBRequest();
                        Map params = new HashMap();
                        params.put(Constants.PERSON_KEY, person.getId());
                        request1.setParams(params);
                        naturalPersonList = personEJB.getNaturalPersonByPerson(request1);
                        for (NaturalPerson n : naturalPersonList) {
                            programOwnerNatural = n;
                        }

                        item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, programOwnerNatural) : new Listcell());
                        item.appendChild(permissionRead ? new ListcellViewButton(adminPage, programOwnerNatural) : new Listcell());
                    } else {
                        ownerNameLegal = person.getLegalPerson().getEnterpriseName();
                        item.appendChild(new Listcell(ownerNameLegal));
                        adminPage = "TabProgramOwnerLegalPerson.zul";

                        EJBRequest request1 = new EJBRequest();
                        Map params = new HashMap();
                        params.put(Constants.PERSON_KEY, person.getId());
                        request1.setParams(params);
                        legalPersonList = personEJB.getLegalPersonByPerson(request1);
                        for (LegalPerson n : legalPersonList) {
                            programOwnerLegal = n;
                        }

                        item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, programOwnerLegal) : new Listcell());
                        item.appendChild(permissionRead ? new ListcellViewButton(adminPage, programOwnerLegal) : new Listcell());
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
        persons = new ArrayList<Person>();
        try {
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_CLASSIFICATION_KEY, Constants.PERSON_CLASSIFICATION_PROGRAM_OWNER);
            request1.setParams(params);
            persons = personEJB.getPersonByCustommer(request1);
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

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("cms.common.cardRequest.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtRequestNumber.setText("");
    }

    @Override
    public List<Person> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}