package com.anjuke.mobile.sign;

import com.cndatacom.campus.netcore.Requests.Ticket;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.ModuleListener;
import com.github.unidbg.arm.HookStatus;
import com.github.unidbg.arm.backend.Backend;
import com.github.unidbg.arm.backend.CodeHook;
import com.github.unidbg.arm.backend.DynarmicFactory;
import com.github.unidbg.arm.backend.UnHook;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.debugger.BreakPointCallback;
import com.github.unidbg.debugger.DebuggerType;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.file.linux.AndroidFileIO;
import com.github.unidbg.hook.HookContext;
import com.github.unidbg.hook.ReplaceCallback;
import com.github.unidbg.hook.hookzz.HookEntryInfo;
import com.github.unidbg.hook.hookzz.HookZz;
import com.github.unidbg.hook.hookzz.HookZzArm32RegisterContext;
import com.github.unidbg.hook.hookzz.IHookZz;
import com.github.unidbg.hook.hookzz.InstrumentCallback;
import com.github.unidbg.hook.hookzz.WrapCallback;
import com.github.unidbg.hook.xhook.IxHook;
import com.github.unidbg.linux.android.AndroidARMEmulator;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.SystemPropertyHook;
import com.github.unidbg.linux.android.XHookImpl;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.BaseVM;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.DvmObject;
import com.github.unidbg.linux.android.dvm.StringObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.linux.android.dvm.VarArg;
import com.github.unidbg.linux.android.dvm.jni.ProxyClassFactory;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmObject;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.MemoryBlock;
import com.github.unidbg.memory.SvcMemory;
import com.github.unidbg.unix.UnixSyscallHandler;
import com.github.unidbg.virtualmodule.android.AndroidModule;
import com.sun.jna.Pointer;

import unicorn.ArmConst;
import unicorn.Unicorn;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.JOptionPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class eSufing extends AbstractJni {

	private final AndroidEmulator emulator;

	private final DvmClass cDataMod;
	private final VM vm;

	private DalvikModule dm;

	private DalvikModule dmLibc;
	
	public byte[] ZSMbytes = new byte[] {};

	private long ZSMSession;

	public UUID currentClientID = UUID.randomUUID();
	
	public String UserAgent = "CCTP/Android8/2075";
	
	public String IPV4 = "1.1.1.1";

	public eSufing() {
		AndroidEmulatorBuilder builder = new AndroidEmulatorBuilder(false) {
			public AndroidEmulator build() {
				return new AndroidARMEmulator(processName, rootDir, backendFactories) {
				};
			}
		};

		emulator = builder
				.setProcessName("com.cndatacom.campus.cdccportalgd").build();
		Memory memory = emulator.getMemory();
		memory.setLibraryResolver(new AndroidResolver(23));

		vm = emulator.createDalvikVM();

		new AndroidModule(emulator, vm).register(memory);

		vm.setVerbose(false);
		vm.setJni(new AbstractJni() {
			@Override
			public DvmObject<?> callObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {

				switch (signature) {
				case "android/content/Context->getFilesDir()Ljava/io/File;":
					return vm.resolveClass("java/io/File").newObject(new File("target"));
				case "java/io/File->getAbsolutePath()Ljava/lang/String;":
					File file = (File) dvmObject.getValue();
					return new StringObject(vm, file.getAbsolutePath());
				}
				return super.callObjectMethod(vm, dvmObject, signature, varArg);
			}

			@Override
			public DvmObject<?> callStaticObjectMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {

				switch (signature) {
				case "android/app/ActivityThread->currentPackageName()Ljava/lang/String;":
					return new StringObject(vm, "com.cndatacom.campus.netcore");
				case "android/app/ActivityThread->currentApplication()Landroid/app/Application;":
					return vm.resolveClass("android/app/Application", vm.resolveClass("android/content/ContextWrapper",
							vm.resolveClass("android/content/Context"))).newObject(signature);
				}

				return super.callStaticObjectMethod(vm, dvmClass, signature, varArg);

			}
		});

		dmLibc = vm.loadLibrary(new File("../unidbg-android/src/main/resources/android/sdk23/lib/libc.so"), true);

		dm = vm.loadLibrary(new File("../unidbg-android/src/test/resources/example_binaries/libdaproxy.so"), true);

		long address = dmLibc.getModule().findSymbolByName("strcmp").getAddress();

		HookZz hook = HookZz.getInstance(emulator);

		hook.replace(address, new ReplaceCallback() {

			@Override
			public HookStatus onCall(Emulator<?> emulator, HookContext context, long originFunction) {
				String arg1 = context.getPointerArg(0).getString(0);
				String arg2 = context.getPointerArg(1).getString(0);

				System.out.println(arg1 + " " + arg2 + " ");

				List<String> Filter = Arrays.asList(new String[] {"ipv4", "local-time"}); 
				
				//2022-03-11 07:54:47
				Filter.forEach(key -> {
					if (arg2.indexOf(key) == 0) {
						final String fakeInput = key;

						MemoryBlock fakeInputBlock = emulator.getMemory().malloc(fakeInput.length(), true);

						fakeInputBlock.getPointer().write(fakeInput.getBytes(StandardCharsets.UTF_8));

						emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, fakeInputBlock.getPointer().peer);

					}
				});

				return HookStatus.RET(emulator, originFunction);

			}

		});
		
		dm.callJNI_OnLoad(emulator);

		cDataMod = vm.resolveClass("com/cndatacom/campus/netcore/DaMod");
	}

	public void destroy() throws IOException {
		emulator.close();
	}
	
	public void setBytes(byte[] b) {
		ZSMbytes = b;
	}
	
	public String checkSum(String in) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest  = md5.digest(in.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //16是表示转换为16进制数
        return new BigInteger(1, digest).toString(16).toLowerCase();
	}
	
	/**
	 * 
	 * Get initial DaMod
	 * 
	 */
	public void loadZSM() {
		try {
			try {
			
			String Body = "tarce log algo auto compstr null value";
			
			URL url = new URL("http://14.146.227.141:7001/ticket.cgi");
			
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			
		    con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", this.UserAgent);  
			con.setRequestProperty("Algo-ID", "00000000-0000-0000-0000-000000000000");
			con.setRequestProperty("Client-ID", currentClientID.toString());
			con.setRequestProperty("CDC-Checksum", checkSum(Body));
			con.setRequestProperty("CDC-SchoolId", "_");
			con.setRequestProperty("CDC-Domain", "_");
			con.setRequestProperty("CDC-Area", "_");
			con.setRequestProperty("Host", "14.146.227.141:7001");
			
	        con.setDoOutput(true);
	        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	        wr.writeBytes(Body);
	        wr.flush();
	        wr.close();
				
	        int len = Integer.parseInt(con.getHeaderField("Content-Length"));
	        ZSMbytes = new byte[len];
	        
	        InputStream ip = con.getInputStream();
	        
	        while(len > 0) {
	        	int read = ip.read(ZSMbytes, ZSMbytes.length - len, len);
	        	
	        	if(read < 0) break;
	        	
	        	len -= read;
	        	
	        }
	        
	
			}catch(Throwable c) {c.printStackTrace();}

			ZSMSession = cDataMod.callStaticJniMethodLong(emulator, "load([B)J", ZSMbytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String encrypt(String in) throws Exception {
		DvmObject<?> r = cDataMod.callStaticJniMethodObject(emulator, "enc(J[B)[B", ZSMSession,
						in.getBytes("UTF-8"));
		return  new String((byte[]) r.getValue());
	}
	
	public String decrypt(String in) {
		DvmObject<?> r2 = cDataMod.callStaticJniMethodObject(emulator, "dec(J[B)[B", ZSMSession, in);
		return new String((byte[]) r2.getValue());
	}

	public static void main(String[] args) throws Exception {
		eSufing signUtil = new eSufing();
		signUtil.loadZSM();

		signUtil.encrypt(new Ticket(signUtil).doAction()); 
		
		//TODO:: Then send post etc....

		signUtil.destroy();
	}

}
