/**
 * @author !@#$%%$#@!!@#$%%$#@!      Chris Steigerwald !@#$%%$#@!!@#$%%$#@!!@#$%%$#@!
 *
 * There's a separate folder with the ThreadOS .class files
 *
 */
import java.io.Console;


/**
 *  Shell Class is main driver class of program and extends Thread.
 */
public class Shell extends Thread
{
    private String ProgramName = "none";
    public Shell()
    {
        SysLib.cout("Welcome to the Shell!~~~");
    }
    public Shell(String args[])
    {
        ProgramName = args[1];
        SysLib.cout("Welcome to the Shell!~~~");
        SysLib.cout("Shell Constructor running. name = " + ProgramName + "\n");
    }

    /**
     *  run() overrides Thread class run method. Prompts user for input, then reads input into
     *  buffer and calls executeInput(), this continues until user types "exit" into prompt. Then
     *  writes memory to disk, and terminates calling thread.
     */
    @Override
    public void run()
    {
        boolean run = true;
        int count = 1;

        while(run)
        {
            // Display user prompt
            SysLib.cout("Shell[" + count + "]]% ");
            // Buffer for holding in user command
            StringBuffer buf = new StringBuffer();
            // Read in user input
            SysLib.cin(buf);
            // Array for holding user input
            String[] input = SysLib.stringToArgs(buf.toString());
            // Validate user input > 1
            if(input.length < 1)
            {
                // continue program, reprint Shell
                continue;
            }

            // If user input "exit" quit program
            if(checkForExit(input))
            {
                break;
            }
            // new line to console
            SysLib.cout("\n");
            // increment count
            count ++;
            executeInput(buf);
        } // end while(run)

        SysLib.cout("Program Terminated");
        // writes back on-memory to disk
        SysLib.sync();
        // terminates calling thread and wakes parent thread
        SysLib.exit();
    }

    /**
     * checkForExit() checks if user type "exit" at prompt to terminate program
     * @param input
     * @return
     */
    boolean checkForExit(String input[])
    {
        // return false to exit program
        return input[0].equals("exit");
    } // end checkForExit()

    /**
     * executeInput() stringBuffer is passed by run(). Will use Java split() to deliminate by ";", "&",
     * or multiple. ";" is a request to run sequentially and ";" to run concurrently. If/else statement
     * will call execute() passing in correct parameters.
     * The following parameters line up with execute
     *          0 - sequential
     *          1 - concurrent
     *          2 - multiple
     * @param buf : StringBuffer
     */
    private void executeInput(StringBuffer buf)
    {
        // User input is ";" sequential
        String[] sequential = buf.toString().split(";");
        // User input is "&" concurrent
        String[] concurrent = buf.toString().split("&");
        // User input is contains multiple arguments
        String multiInput = buf.toString();
        // if/else statement will run command depending on user input
        if (sequential.length > 1)
        {
            execute(sequential, 0);
        }
        else if (concurrent.length > 1)
        {
            execute(concurrent, 1);
        }
        else
        {
            String[] multiString = {multiInput};
            execute(multiString, 2);
        }
    } // end executeInput()

    /**
     * execute() executes threads and processes as selected by user.  Method will run either sequential, concurrent,
     * or a multiple of the two depending on typeOfCommand passed in. Currently typeOfCommand is as follows:
     *              0 - sequential
     *              1 - concurrent
     *              2 - multiple
     *
     * Uses switch statement that is keyed to typeOfCommand to run jobs.
     * @param args : String[]
     * @param typeOfCommand : Int
     */
    private void execute(String[] args, int typeOfCommand)
    {
        switch(typeOfCommand)
        {
            // execute sequential
            case 0:
                for (int i = 0; i < args.length; i++) {
                    String[] commands = SysLib.stringToArgs(args[i]);
                    SysLib.cout(commands[0] + ": \n\t");
                    if (SysLib.exec(commands) < 0)
                    {
                        return;
                    }
                    SysLib.join();
                }
                break;
            // execute concurrent
            case 1:
                int count = 0;
                for (int i = 0; i < args.length; i++)
                {
                    String[] commands = SysLib.stringToArgs(args[i]);
                    SysLib.cout(commands[0] + "\n\t");
                    if (SysLib.exec(commands) < 0)
                    {
                        count--;
                    }
                    else
                    {
                        count++;
                    }
                }
                for (int j = 0; j < count; j++)
                {
                    SysLib.join();
                }
            // execute multiple
            case 2:
                // delimited by ";" for sequential execution
                String[] sequentialCommand = args.toString().split(";");
                // loop through sequentialCommand array
                for (int i = 0; i < sequentialCommand.length; i++)
                {
                    SysLib.cout(sequentialCommand[i] + "\n");
                    // if not delimited by ";" it will delimit by "&" for concurrent
                    String[] concurrentCommand = sequentialCommand[i].split("&");
                    if (concurrentCommand.length == 1)
                    {
                        String seqCommand = sequentialCommand[i];
                        String[] seqArgs = SysLib.stringToArgs(seqCommand);
                        if (SysLib.exec(seqArgs)> 0)
                        {
                            SysLib.join();
                        }
                    }
                    // execute concurrent
                    else
                    {
                        execute(concurrentCommand, 1);
                    }
                }
                break;

            default:
                break;
        } // end switch
    } // end execute()

} // end Shell extends thread