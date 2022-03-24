package server.game;

import commons.Activity;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import server.api.*;
import server.database.ActivityDBController;
import server.database.QuestionDBController;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * The fake application context is used to inject in tests
 * instead of the real application context
 */
public class FakeApplicationContext implements ApplicationContext {

    private FakeSimpMessagingTemplate template;

    /**
     * Creates a new fake application context
     */
    public FakeApplicationContext() {

    }

    /**
     * Sets the FakeSimpMessagingTemplate, so that the GameUpdateManagers of Game objects are initialized with the same
     * FakeSimpMessagingTemplate. This is necessary so that it can be tested whether that messaging template has
     * sent specific messages.
     * @param template the FakeSimpMessagingTemplate used as an argument for all GameUpdateManagers of Game objects
     */
    public void setFakeMessagingTemplate(FakeSimpMessagingTemplate template){
        this.template = template;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getApplicationName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public long getStartupDate() {
        return 0;
    }

    @Override
    public ApplicationContext getParent() {
        return null;
    }

    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return null;
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return null;
    }

    @Override
    public boolean containsLocalBean(String name) {
        return false;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return false;
    }

    @Override
    public int getBeanDefinitionCount() {
        return 0;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return new String[0];
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
        return null;
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType, boolean allowEagerInit) {
        return null;
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type) {
        return new String[0];
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        return new String[0];
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        return new String[0];
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return new String[0];
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return null;
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        return null;
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        return new String[0];
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        return null;
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        return null;
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return null;
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return null;
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return null;
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) throws BeansException {

        if(requiredType.equals(Game.class)) {

            // TODO: not sure if this is the correct way to do this
            ActivityDBController activityDBController = new ActivityDBController(new TestActivityDB());

            // Add some default activities to the database
            activityDBController.getInternalDB().save(new Activity("id1", "imagePath", "1", 1));
            activityDBController.getInternalDB().save(new Activity("id2", "imagePath", "2", 23));
            activityDBController.getInternalDB().save(new Activity("id3", "imagePath", "3", 332));
            activityDBController.getInternalDB().save(new Activity("id4", "imagePath", "4", 4));
            activityDBController.getInternalDB().save(new Activity("id5", "imagePath", "5", 534));
            activityDBController.getInternalDB().save(new Activity("id6", "imagePath", "6", 61));
            activityDBController.getInternalDB().save(new Activity("id7", "imagePath", "7", 71));
            activityDBController.getInternalDB().save(new Activity("id8", "imagePath", "8", 423));
            activityDBController.getInternalDB().save(new Activity("id9", "imagePath", "9", 424));
            activityDBController.getInternalDB().save(new Activity("id10", "imagePath", "10", 10));
            activityDBController.getInternalDB().save(new Activity("id11", "imagePath", "11", 99));
            activityDBController.getInternalDB().save(new Activity("id12", "imagePath", "12", 33));
            activityDBController.getInternalDB().save(new Activity("id13", "imagePath", "13", 22));
            activityDBController.getInternalDB().save(new Activity("id14", "imagePath", "14", 120));
            activityDBController.getInternalDB().save(new Activity("id15", "imagePath", "15", 234));
            activityDBController.getInternalDB().save(new Activity("id16", "imagePath", "16", 4567));
            activityDBController.getInternalDB().save(new Activity("id17", "imagePath", "17", 211));
            activityDBController.getInternalDB().save(new Activity("id18", "imagePath", "18", 2244));
            activityDBController.getInternalDB().save(new Activity("id19", "imagePath", "19", 34));
            activityDBController.getInternalDB().save(new Activity("id20", "imagePath", "20", 320));

            QuestionDBController questionDBController = new QuestionDBController(new TestQuestionDB());
            QuestionGenerator questionGenerator = new QuestionGenerator(new Random(), activityDBController, questionDBController);
            return (T) new Game(new GameUpdateManager(template), questionGenerator);

        }

        return null;

    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return null;
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        return null;
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return null;
    }

    @Override
    public boolean containsBean(String name) {
        return false;
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return null;
    }

    @Override
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return null;
    }

    @Override
    public String[] getAliases(String name) {
        return new String[0];
    }

    @Override
    public void publishEvent(Object event) {

    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return null;
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return null;
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return null;
    }

    @Override
    public Environment getEnvironment() {
        return null;
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        return new Resource[0];
    }

    @Override
    public Resource getResource(String location) {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }
}
