package es.uca.wolidays.frontend;

import java.io.File;

import javax.annotation.PostConstruct;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.uca.wolidays.backend.security.SecurityUtils;
import es.uca.wolidays.frontend.views.LoginView;
import es.uca.wolidays.frontend.views.MisApartamentosView;
import es.uca.wolidays.frontend.views.MisReservasView;
import es.uca.wolidays.frontend.views.NuevoApartamentoView;
import es.uca.wolidays.frontend.views.SignupView;


@SpringViewDisplay
public class MainScreen extends VerticalLayout implements ViewDisplay {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1905072662186258460L;
	private Panel springViewDisplay;
	
	private final VerticalLayout mainLayout = new VerticalLayout();
	private final HorizontalLayout navbarLayout = new HorizontalLayout();
	private final HorizontalLayout buttonsLayout = new HorizontalLayout();
	
	private Button inicioSesion = createNavigationButton("Iniciar sesión", "nav_btn_iniciosesion", LoginView.VIEW_NAME, null);
	private Button registrarse = createNavigationButton("Registrarse","nav_btn_registrarse", SignupView.VIEW_NAME, null);
	private Button misReservas = createNavigationButton("Mis reservas", "nav_btn_misreservas", MisReservasView.VIEW_NAME, VaadinIcons.TAGS);
	private Button registrarApto = createNavigationButton("Nuevo apartamento", "nav_btn_nuevoapartamento", NuevoApartamentoView.VIEW_NAME, VaadinIcons.PLUS_CIRCLE);
	private Button misAptos = createNavigationButton("Mis apartamentos", "nav_btn_misapartamentos", MisApartamentosView.VIEW_NAME, VaadinIcons.HOME);
	private Button perfil = createNavigationButton("Perfil", "nav_btn_perfil", "", VaadinIcons.USER);
	
	
	@Override
	public void attach() {
		super.attach();
		this.getUI().getNavigator().navigateTo("");
	}
	
	@PostConstruct
	void init() {
		
		this.setMargin(false);		
		mainLayout.setMargin(false);	
		
		navbarLayout.setMargin(false);
		navbarLayout.addStyleName("orange");
		navbarLayout.setWidth(100.0f, Unit.PERCENTAGE);
		navbarLayout.setHeight("60px");		
		
		buttonsLayout.setHeight("60px");
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource rsc = new FileResource(new File(basepath + "/resources/img/WolidaysIcon.png"));
		Image tlIcon = new Image(null, rsc);
		tlIcon.addStyleName("icon");
		tlIcon.addClickListener(e -> getUI().getNavigator().navigateTo(""));
		navbarLayout.addComponent(tlIcon);
		navbarLayout.setComponentAlignment(tlIcon, Alignment.MIDDLE_LEFT);
		
		setButtons();
		
		buttonsLayout.addStyleName("margin_buttons");
		navbarLayout.addComponent(buttonsLayout);
		navbarLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_RIGHT);	
		
		mainLayout.addComponent(navbarLayout);
		mainLayout.setComponentAlignment(navbarLayout, Alignment.TOP_CENTER);
		
		springViewDisplay = new Panel();
		springViewDisplay.setSizeFull();
		springViewDisplay.setStyleName(ValoTheme.PANEL_BORDERLESS);
		mainLayout.addComponent(springViewDisplay);
		mainLayout.setExpandRatio(springViewDisplay, 1.0f);
		
		
		addComponent(mainLayout);
	}
	
	public void setButtons() {
		
		cleanButtons();
		
		if(SecurityUtils.isLoggedIn()) {
			
			buttonsLayout.addComponents(misReservas, registrarApto, misAptos, perfil);
			buttonsLayout.setComponentAlignment(misReservas, Alignment.MIDDLE_RIGHT);
			buttonsLayout.setComponentAlignment(registrarApto, Alignment.MIDDLE_RIGHT);
			buttonsLayout.setComponentAlignment(misAptos, Alignment.MIDDLE_RIGHT);
			setPerfilButtonCaption();
			buttonsLayout.setComponentAlignment(perfil, Alignment.MIDDLE_RIGHT);
			
		} else {			
			
			buttonsLayout.addComponents(inicioSesion, registrarse);
			buttonsLayout.setComponentAlignment(inicioSesion, Alignment.MIDDLE_RIGHT);
			buttonsLayout.setComponentAlignment(registrarse, Alignment.MIDDLE_RIGHT);	
			
		}
	}
	
	public void cleanButtons() {
		
		if(!SecurityUtils.isLoggedIn()) {
			
			buttonsLayout.removeComponent(misReservas);
			buttonsLayout.removeComponent(registrarApto);
			buttonsLayout.removeComponent(misAptos);
			buttonsLayout.removeComponent(perfil);
			
			
		} else {			
			
			buttonsLayout.removeComponent(inicioSesion);
			buttonsLayout.removeComponent(registrarse);
			
		}
	}
	
	private Button createNavigationButton(String caption, String id, final String viewName, VaadinIcons icon) {
		
		Button button = new Button(caption);
		
		button.setHeight("40px");
		button.addStyleNames(ValoTheme.BUTTON_BORDERLESS);
		button.addClickListener(event -> getUI().getNavigator().navigateTo(viewName));
		button.setId(id);
		
		if(icon != null) {
			button.setIcon(icon);
		}
		
		return button;
	}
	

	@Override
	public void showView(View view) {
		springViewDisplay.setContent((Component) view);
	}
	
	public void setPerfilButtonCaption() {
		perfil.setCaption(SecurityUtils.getUsername());
	}
	
}
