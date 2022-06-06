package KO.Thread;

import java.util.function.Consumer;

import KO.WorkThread;
import KO.utils.Color;

public class WatchThread extends Thread{
	
	public static Consumer<String> log = a -> System.out.println("[" + Thread.currentThread().getName() + "] " + a);
	
	WorkThread target_thread;
	
	public WatchThread(WorkThread th) {
		target_thread = th;
	}
	
	@Override
	public void run(){
		log.accept(Color.ANSI_PURPLE + "[WatchDog Thread] Starting...." + Color.ANSI_RESET);
		
		int counter = 0;

		while (true) {
			
			
			if(target_thread == null || !target_thread.isAlive() || target_thread.isInterrupted()) {
				
				log.accept("Stop watching thread " + target_thread.getName() + " isAlive: " + target_thread.isAlive() + " Interrupted: " +target_thread.isInterrupted());
				
				break;
			}

			try {
				if (target_thread.State.LoginThreadStage >= 1 && target_thread.State.LoginThreadStage < 4) {
					if (System.currentTimeMillis() - target_thread.State.millToCheck > 16 * 1000) {
						target_thread.destroy();
						
						while(!target_thread.isInterrupted())
							target_thread.interrupt();
						
						break;
					}

					if (System.currentTimeMillis() - target_thread.State.millToCheck > 26 * 1000) {
						System.exit(110);
					}

					if (System.currentTimeMillis() - target_thread.State.millToCheck > 6 * 1000) {
						log.accept(Color.ANSI_RED + "[WatchDog Thread] (WARNNING) " + target_thread.getName() + "Stage " + target_thread.State.LoginThreadStage
								+ " takes " + (System.currentTimeMillis() - target_thread.State.millToCheck) + "ms");
						log.accept(Color.ANSI_RESET);
					}
				} else if(target_thread.State.LoginThreadStage == 4 
						&& target_thread.nextCheckMS < System.currentTimeMillis()) {
					log.accept(target_thread.getName() + "checkAlive timeout...");
					
					while(!target_thread.isInterrupted())
						target_thread.interrupt();
					
					break;
				} else if(target_thread.State.LoginThreadStage == 4 && Long.valueOf(target_thread.getExpire()) <= System.currentTimeMillis() / 1000 - 30) {
					log.accept(target_thread.getName() + "Ticket expired...");
					
					while(!target_thread.isInterrupted())
						target_thread.interrupt();
					
					break;
				} else if(target_thread.State.LoginThreadStage == 4 && System.currentTimeMillis() / 1000 % 60 == 0 && counter-- <= 0) {
					
					counter = 3;
					
					
					log.accept(target_thread.getName() + " Ticket Expire in " + (Long.valueOf(target_thread.getExpire()) -  System.currentTimeMillis() / 1000) + " seconds");
				}
				

				Thread.sleep(200L);
			} catch (InterruptedException c) {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
