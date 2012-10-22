package nl.amis.table.view.bean;


import commonj.sdo.helper.XSDHelper;

import java.util.Hashtable;

import javax.faces.model.DataModel;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import nl.amis.sdo.jpa.entities.Employees;
import nl.amis.sdo.jpa.entities.EmployeesSDO;
import nl.amis.sdo.jpa.services.HrSessionEJB;
import nl.amis.sdo.jpa.services.Service;
import nl.amis.table.view.model.PagedListDataModel;


public class EmployeesBean {
  static {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    XSDHelper.INSTANCE.define(loader.getResourceAsStream("META-INF/wsdl/ServiceException.xsd"),
                              "META-INF/wsdl/");
    XSDHelper.INSTANCE.define(loader.getResourceAsStream("META-INF/wsdl/BC4JService.xsd"),
                              "META-INF/wsdl/");
    XSDHelper.INSTANCE.define(loader.getResourceAsStream("META-INF/wsdl/BC4JServiceCS.xsd"),
                              "META-INF/wsdl/");
    XSDHelper.INSTANCE.define(loader.getResourceAsStream("nl/amis/sdo/jpa/entities/EmployeesSDO.xsd"),
                              "nl/amis/sdo/jpa/entities/");
    XSDHelper.INSTANCE.define(loader.getResourceAsStream("nl/amis/sdo/jpa/entities/DepartmentsSDO.xsd"),
                              "nl/amis/sdo/jpa/entities/");
    XSDHelper.INSTANCE.define(loader.getResourceAsStream("nl/amis/sdo/jpa/services/HrSessionEJBBeanWS.xsd"),
                              "nl/amis/sdo/jpa/services/");
  }

  private DataModel dataModel = null;

  public EmployeesBean() throws NamingException {
    System.out.println("Tablebean initialized");

    final Hashtable env = new Hashtable();
    // WebLogic Server 10.x connection details
    env.put(Context.INITIAL_CONTEXT_FACTORY,
            "weblogic.jndi.WLInitialContextFactory");
    env.put(Context.PROVIDER_URL, "t3://pc100016989:7001");
    final Context context = new InitialContext(env);
    System.out.println("context = " + context);
    dataModel =
        new PagedListDataModel<EmployeesSDO, Employees>((Service)context.lookup("EjbSdoService-HrSessionEJB#nl.amis.sdo.jpa.services.HrSessionEJB"),
                                                        Employees.class, 10);
  }

  public DataModel getDataModel() {
    System.out.println("getDataModel: " + dataModel);
    return dataModel;
  }
}