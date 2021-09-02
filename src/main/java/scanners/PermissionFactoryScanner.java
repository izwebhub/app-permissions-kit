package scanners;

import annotations.PermissionFactory;
import annotations.PermissionMetaData;
import models.Permission;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class PermissionFactoryScanner {

    private String scanPackage = "";
    private boolean debug = false;
    private Set<Permission> permissions = new CopyOnWriteArraySet<>();

    private PermissionFactoryScanner() {
    }

    public static PermissionFactoryScanner getInstance() {
        return new PermissionFactoryScanner();
    }

    @PreAuthorize("")
    @PermissionMetaData(displayName = "", groupName = "")
    public PermissionFactoryScanner setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
        return this;
    }

    public PermissionFactoryScanner setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
        // Don't pull default filters (@Component, etc.):
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter((Class<? extends Annotation>) PermissionFactory.class));
        return provider;
    }

    private void findAnnotatedClasses() {

        if (scanPackage == "") {
            throw new RuntimeException("Please set scanPackage first");
        }

        ClassPathScanningCandidateComponentProvider provider = createComponentScanner();
        for (BeanDefinition beanDef : provider.findCandidateComponents(scanPackage)) {
            printMetadata(beanDef);
        }
    }

    private void printMetadata(BeanDefinition beanDef) {
        try {
            Class<?> cl = Class.forName(beanDef.getBeanClassName());

            for (Method method : cl.getMethods()) {
                if (method.isAnnotationPresent(PreAuthorize.class)) {

                    // Permission Name
                    String permission = method.getAnnotation(PreAuthorize.class).value();
                    String permname = permission.split("'")[1];
                    if (debug) {
                        System.out.println("");
                        System.out.println("");
                        System.out.println("Permission : " + permname);
                    }

                    if (method.isAnnotationPresent(PermissionMetaData.class)) {

                        String displayName = method.getAnnotation(PermissionMetaData.class).displayName();
                        String groupName = method.getAnnotation(PermissionMetaData.class).groupName();

                        Permission availablePerm = new Permission(permname, displayName, groupName);

                        this.permissions.add(availablePerm);

                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Got exception: " + e.getMessage());
        }
    }


    public Set<Permission> getAppPermissions() {
        this.findAnnotatedClasses();
        return this.permissions;
    }
}
