package es.uca.wolidays.frontend.views.gestor;

import javax.annotation.PostConstruct;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = FacturacionView.VIEW_NAME)
public class FacturacionView extends VerticalLayout implements View {
	
	private static final long serialVersionUID = -3089381541889114455L;
	public static final String VIEW_NAME = "facturacion";

	@PostConstruct
	void init() {
		
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}
	
}
