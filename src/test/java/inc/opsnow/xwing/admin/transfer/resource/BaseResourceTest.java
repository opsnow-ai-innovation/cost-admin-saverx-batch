package inc.opsnow.xwing.admin.transfer.resource;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@QuarkusTest
public abstract class BaseResourceTest {

    protected static String TOKEN = "";

    protected static final int FIRST = 0;

    protected static final int CREATE = 100;

    protected static final int READ = 200;

    protected static final int UPDATE = 300;

    protected static final int DELETE = 400;

    protected static String INVALID_STR;
    protected static String VALID_STR;
    protected static Long INVALID_LONG;


    protected static final int LAST = 0;

    @BeforeAll
    protected static void initializedField() {
        TOKEN = ResourceUtil.getResource("token.txt");
        VALID_STR = "VALID_" + UUID.randomUUID();       // FIXME : 선언 대신 생성 메소드로 대체할 것
        INVALID_STR = "INVALID" + UUID.randomUUID();
        INVALID_LONG = ThreadLocalRandom.current().nextLong();


    }

}

