package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import static com.alodiga.cms.web.controllers.ListRateByProductController.program;
import static com.alodiga.cms.web.controllers.ListRateByProgramController.program;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Card;
import com.cms.commons.models.Program;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public class ListCardManagerControllers extends GenericAbstractListController<Card> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private Combobox cmbProgram;
    private UtilsEJB utilsEJB = null;
    private CardEJB cardEJB = null;
    private ProgramEJB programEJB = null;
    private Program program = null;
    private List<Card> card = null;
    private User currentUser;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
            adminPage = "TabCardActivation.zul";
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            cardEJB = (CardEJB) EJBServiceLocator.getInstance().get(EjbConstants.CARD_EJB);
            loadCmbProgram(WebConstants.EVENT_ADD);
//            getData();
            loadDataList(card);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSearch() throws InterruptedException {
        if (validateEmpty()) {
            program = (Program) cmbProgram.getSelectedItem().getValue();
            Sessions.getCurrent().setAttribute(WebConstants.PROGRAM, program);
            getData(program.getId());
            loadDataList(card);
        }
    }
    

    public Boolean validateEmpty() {
        if (cmbProgram.getSelectedItem() == null) {
            cmbProgram.setFocus(true);
            this.showMessage("cms.error.program.notSelected", true, null);
        } else {
            return true;
        }
        return false;
    }

//    public void onChange$cmbProgram() {
//        program = (Program) cmbProgram.getSelectedItem().getValue();
//        Sessions.getCurrent().setAttribute(WebConstants.PROGRAM, program);
//        getData(program.getId());
//    }

    public void getData(Long programId) {
        try {
            EJBRequest prog = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_PROGRAM_ID, programId);
            prog.setParams(params);

            card = cardEJB.getCardByProgram(prog);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        }

    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("cms.menu.cardManager"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }

    public void startListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    public void loadDataList(List<Card> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (Card card : list) {

                    item = new Listitem();
                    item.setValue(card);
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    item.appendChild(new Listcell(card.getCardNumber()));
                    item.appendChild(new Listcell(card.getCardHolder()));
                    item.appendChild(new Listcell(simpleDateFormat.format(card.getExpirationDate())));
                    item.appendChild(new Listcell(card.getCardStatusId().getDescription()));
                    item.appendChild(new ListcellEditButton(adminPage, card));
                    item.appendChild(new ListcellViewButton(adminPage, card, true));
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

    private void showEmptyList() {
        Listitem item = new Listitem();
        item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.setParent(lbxRecords);
    }

    private void loadCmbProgram(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Program> programs;
        try {
            programs = programEJB.getProgram(request1);
            loadGenericCombobox(programs, cmbProgram, "name", evenInteger, Long.valueOf(0));
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

    @Override
    public List<Card> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void getData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
