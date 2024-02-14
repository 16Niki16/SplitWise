package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateGroupTest {
    private CreateGroup create;
    private ReaderWriterCreator friends;
    private ReaderWriterCreator group;
    private ReaderWriterCreator exc;
    private String friend;
    private String groups;
    private String except;
    private Command command;

    @BeforeEach
    void setUp() {
        except = "";
        friend = """
            niki|niki123|pepi 10.00,kolio 0.00,ili 0.00,koki 5.00
            kolio|kolio123|niki 0.00
            pepi|pepi123|niki -10.00""";
        groups = """
            firstGroup|niki123 0.00,niki 0.00,kolio 0.00
            secondGroup|niki123 0.00,niki 0.00,kolio 0.00""";
        exc = mock();
        friends = mock();
        group = mock();

        create = new CreateGroup(friends, group, exc);
    }

    @Test
    void testCreateGroupValid() {
        String groupTest = groups;
        command = CommandCreator.newCommand("niki create-group thirdGroup kolio pepi");
        when(friends.getRead()).thenAnswer(x -> new StringReader(friend));
        when(friends.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(friends.getAppend()).thenAnswer(x -> new StringWriter());
        when(group.getRead()).thenAnswer(x -> new StringReader(groupTest));
        when(group.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(group.getAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(create.createGroup(command), "Group is successfully created!");
    }

    @Test
    void testCreateGroupExceptions() {
        String groupTest = groups;
        command = CommandCreator.newCommand("niki create-group firstGroup kolio pepi");
        when(friends.getRead()).thenAnswer(x -> new StringReader(friend));
        when(friends.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(friends.getAppend()).thenAnswer(x -> new StringWriter());
        when(group.getRead()).thenAnswer(x -> new StringReader(groupTest));
        when(group.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(group.getAppend()).thenAnswer(x -> new StringWriter());
        when(exc.getRead()).thenAnswer(x -> new StringReader(except));
        when(exc.getAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(create.createGroup(command),
            "Group with this name already exist.", "Wrong testing in group exist!");
    }
}
