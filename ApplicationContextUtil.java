import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextUtil implements ApplicationContextAware {

    /*
     *  Spring Bean 이 아닌 파일에서,
     *  Spring Container 에서 관리하는 Bean 을 사용할 때 도움을 주는 Util 파일이다.
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    /**
     * 호출하는 applicationContext 를 찾아서, 반환한다.
     *
     */
    public static ApplicationContext getApplicationContext() {
        return getApplicationContext();
    }

    /**
     *
     * @param beanName
     * @return Bean
     *
     * applicationContext 로부터 Spring Bean 을 찾아서 반환한다.
     */
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }
}
