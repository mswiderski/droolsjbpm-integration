package org.kie.server.integrationtests.optaplanner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.server.api.KieServerConstants;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.marshalling.MarshallerFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.Wrapped;
import org.kie.server.api.model.instance.SolverInstance;
import org.kie.server.integrationtests.shared.KieServerBaseIntegrationTest;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class MarshallerTest {

    private static final Logger logger = LoggerFactory.getLogger(MarshallerTest.class);

    private static final String CLASS_CLOUD_BALANCE  = "org.kie.server.testing.CloudBalance";
    private static final String CLASS_CLOUD_COMPUTER = "org.kie.server.testing.CloudComputer";
    private static final String CLASS_CLOUD_PROCESS  = "org.kie.server.testing.CloudProcess";
    private static final String CLASS_CLOUD_GENERATOR = "org.kie.server.testing.CloudBalancingGenerator";

    private static final ReleaseId kjar1 = new ReleaseId("org.kie.server.testing", "cloudbalance", "1.0.0.Final" );

    protected static KieContainer kieContainer;

    private Set<Class<?>> extraClasses;

    private Class<?> solutionClass;

    @BeforeClass
    public static void deployArtifacts() {
        KieServerBaseIntegrationTest.buildAndDeployCommonMavenParent();
        KieServerBaseIntegrationTest.buildAndDeployMavenProject(ClassLoader.class.getResource("/kjars-sources/cloudbalance").getFile());

        kieContainer = KieServices.Factory.get().newKieContainer(kjar1);
    }

    @Before
    public void setup() throws Exception{
        extraClasses = new HashSet<Class<?>>();

        solutionClass = Class.forName(CLASS_CLOUD_BALANCE, true, kieContainer.getClassLoader());

        extraClasses.add(solutionClass);
        extraClasses.add(Class.forName(CLASS_CLOUD_COMPUTER, true, kieContainer.getClassLoader()));
        extraClasses.add(Class.forName(CLASS_CLOUD_PROCESS, true, kieContainer.getClassLoader()));

    }

    public Solution loadPlanningProblem( int computerListSize, int processListSize ) {
        Solution problem = null;
        try {
            Class<?> cbgc = kieContainer.getClassLoader().loadClass( CLASS_CLOUD_GENERATOR );
            Object cbgi = cbgc.newInstance();

            Method method = cbgc.getMethod( "createCloudBalance", int.class, int.class );
            problem = (Solution) method.invoke( cbgi, computerListSize, processListSize );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Exception trying to create cloud balance unsolved problem.");
        }
        return problem;
    }

    @Test
    public void testJAXBMarshallerRoundTripping() {
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.JAXB, kieContainer.getClassLoader());

        testMarshallerRoundTripping(marshaller);
    }

    @Test
    public void testJSONMarshallerRoundTripping() {
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.JSON, kieContainer.getClassLoader());

        testMarshallerRoundTripping(marshaller);
    }

    @Test
    public void testXSTREAMMarshallerRoundTripping() {
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.XSTREAM, kieContainer.getClassLoader());

        testMarshallerRoundTripping(marshaller);
    }

    protected void testMarshallerRoundTripping(Marshaller marshaller) {
        Solution solution = loadPlanningProblem(2, 2);

        String content = marshaller.marshall(solution);
        logger.debug("Generated string representation of {} is '{}'", solution, content);
        assertNotNull(content);

        Object solution1 = marshaller.unmarshall(content, solutionClass);

        assertNotNull(solution1);

        Object computerList = valueOf(solution1, "computerList");
        assertNotNull(computerList);
        assertTrue(computerList instanceof List);
        assertEquals(2, ((List)computerList).size());

        Object processList = valueOf(solution1, "processList");
        assertNotNull(processList);
        assertTrue(processList instanceof List);
        assertEquals(2, ((List)processList).size());
    }

    @Test
    public void testJAXBMarshallerServiceResponseRoundTripping() {
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.JAXB, kieContainer.getClassLoader());

        testMarshallerServiceResponseRoundTripping(marshaller);
    }

    @Test
    public void testJSONMarshallerServiceResponseRoundTripping() {
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.JSON, kieContainer.getClassLoader());

        testMarshallerServiceResponseRoundTripping(marshaller);
    }

    @Test
    public void testXSTREAMMarshallerServiceResponseRoundTripping() {
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.XSTREAM, kieContainer.getClassLoader());

        testMarshallerServiceResponseRoundTripping(marshaller);
    }

    protected void testMarshallerServiceResponseRoundTripping(Marshaller marshaller) {
        Solution solution = loadPlanningProblem(2, 2);

        SolverInstance solverInstance = new SolverInstance();
        solverInstance.setBestSolution(solution);
        solverInstance.setContainerId("container");
        solverInstance.setPlanningProblem(solution);
        solverInstance.setScore(HardSoftScore.valueOf(100, 200));


        ServiceResponse<SolverInstance> serviceResponse = new ServiceResponse<SolverInstance>();
        serviceResponse.setResult(solverInstance);
        serviceResponse.setType(ServiceResponse.ResponseType.SUCCESS);

        String content = marshaller.marshall(serviceResponse);
        logger.debug("Generated string representation of {} is '{}'", serviceResponse, content);
        assertNotNull(content);

        Object serviceResponse1 = marshaller.unmarshall(content, ServiceResponse.class);

        assertNotNull(serviceResponse);
        assertTrue(serviceResponse1 instanceof ServiceResponse);
        assertEquals(ServiceResponse.ResponseType.SUCCESS, ((ServiceResponse)serviceResponse1).getType());

        Object solverInstance1 =  ((ServiceResponse)serviceResponse1).getResult();
        assertNotNull(solverInstance1);
        assertTrue(solverInstance1 instanceof SolverInstance);

        Object solution1 = ((SolverInstance) solverInstance1).getBestSolution();

        Object computerList = valueOf(solution1, "computerList");
        assertNotNull(computerList);
        assertTrue(computerList instanceof List);
        assertEquals(2, ((List)computerList).size());

        Object processList = valueOf(solution1, "processList");
        assertNotNull(processList);
        assertTrue(processList instanceof List);
        assertEquals(2, ((List)processList).size());
    }

    @Test
    public void testJAXBMarshallerSolverInstanceRoundTripping() {
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.JAXB, kieContainer.getClassLoader());

        testMarshallerSolverInstanceRoundTripping(marshaller);
    }

    @Test
    public void testJSONMarshallerSolverInstanceRoundTripping() {
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.JSON, kieContainer.getClassLoader());

        testMarshallerSolverInstanceRoundTripping(marshaller);
    }

    @Test
    public void testXSTREAMMarshallerSolverInstanceRoundTripping() {
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.XSTREAM, kieContainer.getClassLoader());

        testMarshallerSolverInstanceRoundTripping(marshaller);
    }

    protected void testMarshallerSolverInstanceRoundTripping(Marshaller marshaller) {
        Solution solution = loadPlanningProblem(2, 2);

        SolverInstance solverInstance = new SolverInstance();
        solverInstance.setBestSolution(solution);
        solverInstance.setContainerId("container");
        solverInstance.setPlanningProblem(solution);
        solverInstance.setScore(HardSoftScore.valueOf(100, 200));
        solverInstance.setSolverId("111111");
        solverInstance.setStatus(SolverInstance.SolverStatus.SOLVING);

        String content = marshaller.marshall(solverInstance);
        logger.debug("Generated string representation of {} is '{}'", solverInstance, content);
        assertNotNull(content);

        Object solverInstance1 = marshaller.unmarshall(content, SolverInstance.class);

        assertNotNull(solverInstance1);
        assertTrue(solverInstance1 instanceof SolverInstance);

        Object solution1 = ((SolverInstance) solverInstance1).getBestSolution();

        Object computerList = valueOf(solution1, "computerList");
        assertNotNull(computerList);
        assertTrue(computerList instanceof List);
        assertEquals(2, ((List)computerList).size());

        Object processList = valueOf(solution1, "processList");
        assertNotNull(processList);
        assertTrue(processList instanceof List);
        assertEquals(2, ((List)processList).size());
    }

    protected Object valueOf(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    public void testJAXBMarshallerSolverInstanceMapRoundTripping() {
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.JAXB, kieContainer.getClassLoader());

        testMarshallerSolverInstanceMapRoundTripping(marshaller);
    }

    @Test
    public void testJSONMarshallerSolverInstanceMapRoundTripping() {
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.JSON, kieContainer.getClassLoader());

        testMarshallerSolverInstanceMapRoundTripping(marshaller);
    }

    @Test
    public void testXSTREAMMarshallerSolverInstanceMapRoundTripping() {
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.XSTREAM, kieContainer.getClassLoader());

        testMarshallerSolverInstanceMapRoundTripping(marshaller);
    }

    protected void testMarshallerSolverInstanceMapRoundTripping(Marshaller marshaller) {
        Solution solution = loadPlanningProblem(2, 2);

        SolverInstance solverInstance = new SolverInstance();
        solverInstance.setBestSolution(solution);
        solverInstance.setContainerId("container");
        solverInstance.setPlanningProblem(solution);
        solverInstance.setScore(HardSoftScore.valueOf(100, 200));
        solverInstance.setSolverId("111111");
        solverInstance.setStatus(SolverInstance.SolverStatus.SOLVING);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("one", solution);
        map.put("two", solution);

        String content = marshaller.marshall(map);
        logger.debug("Generated string representation of {} is '{}'", solverInstance, content);
        assertNotNull(content);

        Object solverInstance1 = marshaller.unmarshall(content, Map.class);

        if (solverInstance1 instanceof Wrapped) {
            solverInstance1 = ((Wrapped) solverInstance1).unwrap();
        }

        assertNotNull(solverInstance1);
        assertTrue(solverInstance1 instanceof Map);

        assertEquals(2, ((Map) solverInstance1).size());
        assertTrue(((Map) solverInstance1).containsKey("one"));
        assertTrue(((Map) solverInstance1).containsKey("two"));

        assertTrue(((Map) solverInstance1).get("one") instanceof Solution);
        assertTrue(((Map) solverInstance1).get("two") instanceof Solution);

    }
}
