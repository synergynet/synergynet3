package synergynet3.web.apps.numbernet.client.expressionview;


import java.util.List;

import synergynet3.web.apps.numbernet.client.service.NumberNetService;
import synergynet3.web.apps.numbernet.shared.Expression;
import synergynet3.web.apps.numbernet.shared.Participant;
import synergynet3.web.apps.numbernet.shared.TableTarget;
import synergynet3.web.commons.client.dialogs.MessageDialogBox;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;


public class ExpressionView extends VerticalPanel implements SelectionHandler<TreeItem> {

	private TreeItem trtmByTarget;
	private TreeItem trtmByTable;
	private TreeItem trtmByPerson;
	private VerticalPanel verticalPanel;

	public ExpressionView() {
		super();
		setSpacing(5);
		
		Button btnRefresh = new Button("Refresh");
		btnRefresh.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				refreshButtonPressed();
			}
		});
		add(btnRefresh);
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(10);
		add(horizontalPanel);
		horizontalPanel.setSize("517px", "279px");
		
		Tree tree = new Tree();
		tree.addSelectionHandler(this);
		horizontalPanel.add(tree);
		
		trtmByPerson = new TreeItem("By Person");
		tree.addItem(trtmByPerson);
		
		trtmByTable = new TreeItem("By Table");
		tree.addItem(trtmByTable);
		
		trtmByTarget = new TreeItem("By Target");
		tree.addItem(trtmByTarget);
		
		verticalPanel = new VerticalPanel();
		horizontalPanel.add(verticalPanel);
	}
	
	protected void refreshButtonPressed() {
		loadByTargetItems();
		loadParticipantItems();
	}

	private void loadParticipantItems() {
		NumberNetService.Util.getInstance().getAllParticipants(new AsyncCallback<List<Participant>>() {

			@Override
			public void onFailure(Throwable caught) {
				new MessageDialogBox(caught.getMessage()).show();
			}

			@Override
			public void onSuccess(List<Participant> result) {
				trtmByPerson.removeItems();
				for(Participant p : result) {
					TreeItem person = new TreeItem(p.getName());	
					person.setUserObject("person");
					trtmByPerson.addItem(person);
				}
			}
		});
	}

	private void loadByTargetItems() {
		
		NumberNetService.Util.getInstance().getTableTargets(new AsyncCallback<List<TableTarget>>() {

			@Override
			public void onFailure(Throwable caught) {
				new MessageDialogBox(caught.getMessage()).show();
			}

			@Override
			public void onSuccess(List<TableTarget> result) {
				trtmByTarget.removeItems();
				trtmByTable.removeItems();
				for(TableTarget tt : result) {
					if(tt.getTarget() != null) {
						double target = tt.getTarget();	
						TreeItem targetTreeItem = new TreeItem(target + "");
						targetTreeItem.setUserObject("target");
						trtmByTarget.addItem(targetTreeItem);
					}
					
					String table = tt.getTable();

					TreeItem tableTreeItem = new TreeItem(table);
					tableTreeItem.setUserObject("table");
					trtmByTable.addItem(tableTreeItem);
				}
			}
		});		
		
	}

	public String getTitle() {
		return "Expression Watcher";
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem itemSelected = event.getSelectedItem();
		if(itemSelected.getUserObject() == null) return;
		String type = (String) itemSelected.getUserObject();		
		if(type.equals("table")) {
			loadExpressionCollectionViewForTable(itemSelected.getText());
		}else if(type.equals("person")) {
			loadExpressionCollectionViewForPerson(itemSelected.getText());
		}else if(type.equals("target")) {
			loadExpressionCollectionViewForTarget(Double.parseDouble(itemSelected.getText()));			
		}
	}

	private void loadExpressionCollectionViewForTarget(double parseDouble) {
		NumberNetService.Util.getInstance().getExpressionsForTarget(parseDouble, new AsyncCallback<List<Expression>>() {

			@Override
			public void onFailure(Throwable caught) {
				new MessageDialogBox(caught.getMessage()).show();
			}

			@Override
			public void onSuccess(List<Expression> result) {
				loadCollectionView(result);
			}
		});
	}

	private void loadExpressionCollectionViewForPerson(String text) {
		NumberNetService.Util.getInstance().getExpressionsForPerson(text, new AsyncCallback<List<Expression>>() {

			@Override
			public void onFailure(Throwable caught) {
				new MessageDialogBox(caught.getMessage()).show();
			}

			@Override
			public void onSuccess(List<Expression> result) {
				loadCollectionView(result);
			}
		});
	}

	private void loadExpressionCollectionViewForTable(String table) {
		NumberNetService.Util.getInstance().getExpressionsForTable(table, new AsyncCallback<List<Expression>>() {

			@Override
			public void onFailure(Throwable caught) {
				new MessageDialogBox(caught.getMessage()).show();
			}

			@Override
			public void onSuccess(List<Expression> result) {
				loadCollectionView(result);
			}
		});
	}

	protected void loadCollectionView(List<Expression> result) {
		if(verticalPanel.getWidgetCount() > 0) {
			verticalPanel.remove(0);
		}
		
		ExpressionCollectionView ecv = new ExpressionCollectionView();
		ecv.setSize("394px", "");		
		ecv.setExpressionCollection(result);
		verticalPanel.add(ecv);
	}
}
