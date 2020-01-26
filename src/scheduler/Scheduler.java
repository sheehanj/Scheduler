// John Sheehan
package scheduler;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class Scheduler
{
	public static void main(String[] args) throws IOException
	{
		//method call to createReadyQueue
		Process readyQueue[] = createReadyQueue();
		
		//sort readyQueue by priority, highest first
		Arrays.sort(readyQueue, new SortByPriority());
		
		//implement priority high alogorithm(assigned)
		int currentTime = 0;
		
		for(int i = 0; i < readyQueue.length; i++)
		{
			
			//if process has arrived and is not complete, complete process
			//algorithm is non-preemptive
			if(readyQueue[i].getArrival() <= currentTime && !readyQueue[i].isComplete())
			{
				//complete process, increment time, calculate wait time and resposne time
				readyQueue[i].setStartTime(currentTime);
				readyQueue[i].setWaitTime(currentTime - readyQueue[i].getArrival());
				currentTime += readyQueue[i].getBurst();
				readyQueue[i].setEndTime(currentTime);
				readyQueue[i].setResponseTime(currentTime - readyQueue[i].getArrival());
				readyQueue[i].complete();
				
				//set index to zero to handle case of passing processes that had not arrived
				i = 0;
				
			}
		}
		
		
		//calculate average wait time and average response time
		double waitSum = 0;
		double responseSum = 0;
		for (int i = 0; i < readyQueue.length; i++)
		{
			waitSum += readyQueue[i].getWaitTime();
			responseSum += readyQueue[i].getResponseTime();
		}
		
		
		double avgWaitTime = waitSum / readyQueue.length;
		double avgResponseTime = responseSum / readyQueue.length;
		
		//write results to file
		BufferedWriter output = new BufferedWriter(new FileWriter("output3.txt"));
		
		for(int i = 0; i < readyQueue.length; i++)
		{
			readyQueue[i].print(output);
		}
		
		String str1 = "Average Wait time: " + avgWaitTime + "\n";
		output.write(str1);
		String str2 = "Average Response time: " + avgResponseTime + "\n";
		output.write(str2);
		
		output.close();
	}	
	
	//read input from file and populate ready queue
	//returns array of processes
	public static Process[] createReadyQueue()
	{
		try
		{
			Scanner input = new Scanner(Paths.get("input3.txt"));
			
			//first line is number of processes
			int processCount = input.nextInt();
			input.nextLine();
			
			//second line indicates if round robin or pre-emptive, and the time quantum
			boolean preemptive = false;
			if (input.nextInt() == 1)
				preemptive = true;
			
			//dont need time quantum for my assigned scheduling algorithm
			//int quantum = input.nextInt();
			input.nextLine();
			
			//create the ready queue
			Process readyQueue[] = new Process[processCount];
		
			//rest of file will be taken as proccesses
			//assumes number of processes in line 1 is correct
			for(int i = 0; i < processCount; i++)
			{
				int _arrival = input.nextInt();
				int _burst = input.nextInt();
				int _priority = input.nextInt();
				Process p = new Process(i+1, _arrival, _burst, _priority);
				readyQueue[i] = p;
				if(input.hasNextLine())
					input.nextLine();
			}
	
			input.close();
			return readyQueue;
		}
		catch(IOException ex)
		{
			System.out.println("Unable to open file");
			return null;
		}
	}	
}

//class to represent a process
class Process
{
	//attributes
	private int pid;
	private int arrival;
	private int burst;
	private int priority;
	private boolean complete;
	private int startTime;
	private int endTime;
	private int waitTime;
	private int responseTime;
	
	//constructor
	public Process(int _pid, int _arrival, int _burst, int _priority)
	{
		pid = _pid;
		arrival = _arrival;
		burst = _burst;
		priority = _priority;
		complete = false;
		startTime = 0;
		endTime = 0;
		waitTime = 0;
		responseTime = 0;
	}
	
	//getters
	int getPid()
	{
		return pid;
	}
	int getArrival()
	{
		return arrival;
	}
	int getBurst()
	{
		return burst;
	}
	int getPriority()
	{
		return priority;
	}
	boolean isComplete()
	{
		return complete;
	}
	int getStartTime()
	{
		return startTime;
	}
	int getEndTime()
	{
		return endTime;
	}
	int getWaitTime()
	{
		return waitTime;
	}
	int getResponseTime()
	{
		return responseTime;
	}
	
	//setters
	void complete()
	{
		complete = true;
	}
	void setStartTime(int _startTime)
	{
		startTime = _startTime;
	}
	void setEndTime(int _endTime)
	{
		endTime = _endTime;
	}
	void setWaitTime(int _waitTime)
	{
		waitTime = _waitTime;
	}
	void setResponseTime(int _responseTime)
	{
		responseTime = _responseTime;
	}
	
	//write information to file
	void print(BufferedWriter output)
	{
		String str = "Process ID: " + pid + " Start Time: " + startTime + " End Time: " + endTime + "\n";
		try
		{
			output.write(str);
		}
		catch(IOException ex)
		{
			System.out.println("Unable to open file");
		}
		
	}
}

//use comparator interface to sort readyQueue by priority
class SortByPriority implements Comparator<Process> 
{ 
    // Used for sorting in ascending order of 
    // roll number 
    public int compare(Process a, Process b) 
    { 
        return b.getPriority() - a.getPriority(); 
    } 
}

