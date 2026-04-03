package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateGroupTest {
    private CreateGroup create;
    private String friend;
    private String groups;
    private String except;
    private CommandLine command;

    @BeforeEach
    void setUp() {
        except = "";
        friend = """
            niki|niki123|pepi 10.00,kolio 0.00,ili 0.00,koki 5.00|BGN
            kolio|kolio123|niki 0.00|BGN
            pepi|pepi123|niki -10.00|BGN""";
        groups = """
            firstGroup|niki123 0.00,niki 0.00,kolio 0.00
            secondGroup|niki123 0.00,niki 0.00,kolio 0.00""";
        ReaderWriterCreator friends = mock();
        ReaderWriterCreator group = mock();

        create = new CreateGroup(friends, group);

        when(friends.getRead()).thenAnswer(x -> new StringReader(friend));
        when(friends.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(friends.getAppend()).thenAnswer(x -> new StringWriter());
        when(group.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(group.getAppend()).thenAnswer(x -> new StringWriter());
        when(group.getRead()).thenAnswer(x -> new StringReader(groups));
    }

    @Test
    void testCreateGroupValid() {
        command = CommandCreator.newCommand("niki create-group thirdGroup kolio pepi");
        assertEquals(create.createGroup(command), "Group is successfully created!");
    }

    @Test
    void testCreateGroupExceptions() {
        command = CommandCreator.newCommand("niki create-group firstGroup kolio pepi");
        assertEquals(create.createGroup(command),
            "Group with this name already exist.", "Wrong testing in group exist!");
    }

    @Test
    void testFriendNotRegisteredException(){
        command = CommandCreator.newCommand("niki create-group firstGroup kolio unknown");
        assertEquals(create.createGroup(command), "These people are still not registered: unknown",
                "Wrong check all people exist checking!");
    }

    @Test
    void testYourselfException(){
        command = CommandCreator.newCommand("niki create-group firstGroup kolio niki");
        assertEquals(create.createGroup(command),"You are trying to add yourself second time in a group!",
                "Mistake in checking add yourself second time!");
    }
}
