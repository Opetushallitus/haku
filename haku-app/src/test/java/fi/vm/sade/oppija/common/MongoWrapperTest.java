package fi.vm.sade.oppija.common;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MongoWrapperTest {

    private MongoWrapper mongoWrapper;
    private final String NAME = "test-database";
    private Mongo mockMongo;
    private DB dbMock;
    private DBCollection mockCollection;

    @Before
    public void setUp() throws Exception {
        mockMongo = mock(Mongo.class);
        dbMock = mock(DB.class);
        mockCollection = mock(DBCollection.class);
        when(mockMongo.getDB(NAME)).thenReturn(dbMock);
        when(mockMongo.debugString()).thenReturn("mock debug string");
        when(dbMock.getCollection(NAME)).thenReturn(mockCollection);
        mongoWrapper = new MongoWrapper(mockMongo, NAME);
    }

    @Test
    public void close() {
        mongoWrapper.close();
        verify(mockMongo, times(1)).close();
    }

    @Test
    public void testDropDatabase() {
        mongoWrapper.dropDatabase();
        verify(dbMock, times(1)).dropDatabase();

    }

    @Test
    public void testGetCollection() {
        assertEquals(mockCollection, mongoWrapper.getCollection(NAME));
    }
}
