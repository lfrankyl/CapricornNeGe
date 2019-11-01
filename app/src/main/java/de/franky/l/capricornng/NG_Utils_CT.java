package de.franky.l.capricornng;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import androidx.core.content.ContextCompat;
import androidx.core.os.EnvironmentCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
* <p>Methoden zum Aufspueren von SD-Karten und USB-Geraeten.</p>
* <ul>
* <li>{@link #getDevices(Context)} liefert eine Liste aller Geraete inklusive der
* internen Daten-Partition und ggf. nicht eingesteckter Geraete.</li>
* <li>{@link #getExternalStorage(Context)} liefert eine Liste aller gerade
* nutzbaren Speicherorte ausser /data, also Geraetespeicher, SDs und USB.</li>
* <li>{@link #getStorage(Context)} liefert diese Liste inklusive dem
* internen Speicher /data</li>
* </ul>
*
* <p>Diese Methoden puffern die Listen und lauschen von selbst nach Aenderungen durch Einstecken
* und entnehmen von SD-Karten und USB-Geraeten. Damit der dazu noetige BroadcastReceiver
* am Ende freigegeben wird, muss {@code setUseReceiver(android.content.Context, false)}
* aufgerufen werden, sinnvollerweise in onDestroy() der Haupt-Activity. Will man keinen Receiver
* haben, muss man vor dem ersten Aufruf einer der get-Methoden {@code setUseReceiver(null, false)}
* aufrufen. Will man selbst einen Receiver basteln, stellt dazu {@link #getRescanIntentFilter()} den
* passenden IntentFilter bereit. Soll der eigene Receiver den dieser Klasse abloesen (hat man also
* setUse...(...false) aufgerufen), fuehrt {@link #initDevices(Context)} einen Rescan
* der Geraete und Neuaufbau der Listen durch.</p>
* <p> Damit der Zugriff auf die Speichermedien klappt, muss im Manifest stehen:</p>
<pre><code>{@code
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="18" />
}</code></pre>
* <p>Wenn das auch auf Geraeten mit CyanogenMod laufen soll, darf man anders als in der
* Android-Doku beschrieben allerdings das maxSdkVersion <b>nicht</b> angeben.
*
* <h2>Hintergrund</h2>
* <p>Ab Android 4 verlaeuft die Verwaltung von SD-Karten etwas einfacher als in alten Versionen,
* auch weil mit Android 3 die unified-Architektur eingefuehrt wurde. Android selbst liefert
* allerdings erst ab 4.4 in {@link Context#getExternalFilesDirs(String)}
* eine Liste von Pfaden zurueck, und die noch immer ohne USB-Geraete und mit den Pfaden in
* /Android/data statt zum Root.</p>
* <p>Ab Android 2 konnte man die Pfade ueber vold.fstab herausfinden, doch das ist ab Android 4.3
* nicht mehr implementiert. Die Suche ueber /proc/mounts verlaeuft aufgrund der unuebersichtlichen
* fuse-Eintraege nicht gut.</p>
* <p> Eine Loesung gibt es ab Android 4.0, naemlich eine versteckte Methode im
* {@link StorageManager}, die hier per Reflection herausgefuehrt ist.</p>
*
* @author Joerg Wirtgen (jow@ct.de)
*
* @version 0.9
*
*/
public class NG_Utils_CT 
{
	private static final String TAG = "Cpc_Environment4";
	private final static String TYPE_PRIMARY = "primaer";
	private final static String TYPE_INTERNAL = "intern";
	private final static String TYPE_SD = "MicroSD";
	private final static String TYPE_USB = "USB";
	private final static String TYPE_UNKNOWN = "unbekannt";
	private final static String WRITE_NONE = "none";
	private final static String WRITE_READONLY = "readonly";
	private final static String WRITE_APPONLY = "apponly";
	private final static String WRITE_FULL = "readwrite";
	private static Device[] devices, externalstorage, storage;
	private static BroadcastReceiver receiver;
	private static String userDir;
	/**
	* Liefert ein Array aller bekannten Geraete zurueck. Das sind:
	* <ul>
	* <li>ggf. die alte Partition /data (wenn nicht gleich dem naechsten)</li>
	* <li>der Geraetespeicher ("External")</li>
	* <li>SD-Karten</li>
	* <li>USB-Geraete</li>
	* </ul>
	* Die Liste enthaelt keine null-Eintraege, aber viele Android-Versionen liefern hier auch
	* leere Slots und unbenutzte USB-Ports. Ob ein Device eingelegt/nutzbar ist, erfaehrt
	* man per {@link NG_Utils_CT.Device#isAvailable()}. Die Liste ist
	* somit eher als Geraete-Uebersicht nuetzlich.
	*
	* @see NG_Utils_CT.Device
	*
	* @param context Context des Aufrufers, also this oder in Fragments getActivity()
	* @return eine Liste der passenden {@link NG_Utils_CT.Device}
	*/
	public static Device[] getDevices(Context context) 
	{
		if (devices==null) 
			initDevices(context);
		return devices;
	}
	/**
	* Liefert eine Liste aller Speicherorte ausser dem internen /data
	* <ul>
	* <li>Index 0: der Geraetespeicher</li>
	* <li>ggf. ab Index 1: SD-Karten</li>
	* <li>ggf: USB-Geraete</li>
	* </ul>
	* Die Liste enthaelt keine null-Eintraege, und auch nicht eingesteckte Slots werden nicht
	* aufgenommen. Die Liste ist somit nuetzlich zum Festlegen eines Datei-Speicherorts,
	* wenn andere Apps Zugriff drauf haben duerfen.
	*
	* @see NG_Utils_CT.Device
	*
	* @param context Context des Aufrufers, also this oder in Fragments getActivity()
	* @return eine Liste der passenden {@link NG_Utils_CT.Device}
	*/
	public static Device[] getExternalStorage(Context context) 
	{
		if (devices==null) 
			initDevices(context);
		return externalstorage;
	}
	/**
	* Liefert eine Liste aller Speicherorte inklusive dem internen
	* <ul>
	* <li>der interne Speicher</li>
	* <li>der Geraetespeicher</li>
	* <li>SD-Karten</li>
	* <li>USB-Geraete</li>
	* </ul>
	* Die Liste enthaelt keine null-Eintraege, und auch nicht eingesteckte Slots werden nicht
	* aufgenommen. Die Liste ist somit nuetzlich zum Festlegen eines Datei-Speicherorts,
	* wobei Speichern in Index 0 verhindert, dass andere Apps die Daten lesen koennen.
	*
	* @see NG_Utils_CT.Device
	*
	* @param context Context des Aufrufers, also this oder in Fragments getActivity()
	* @return eine Liste der passenden {@link NG_Utils_CT.Device}
	*/
	public static Device[] getStorage(Context context) 
	{
		if (devices==null) 
			initDevices(context);
		return storage;
	}
	/**
	* Wer sich in das An- und Abmelden von SD-Karten und USB-Geraeten einhaengen will,
	* findet hier den noetigen IntentFilter dafuer. Diese Klasse hier traegt sich selbst ein, da
	* muss man also nichts machen.
	*
	* @return ein IntentFilter fuers Ein- und Ausstecken von USB/SD-Speicher
	*/
	private static IntentFilter getRescanIntentFilter()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL); // rausgenommen
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED); // wieder eingesetzt
		filter.addAction(Intent.ACTION_MEDIA_REMOVED); // entnommen
		filter.addAction(Intent.ACTION_MEDIA_SHARED); // per USB am PC
		// geht ohne folgendes nicht, obwohl das in der Doku nicht so recht steht
		filter.addDataScheme("file");
		/*
		* die folgenden waren zumindest bei den bisher mit USB getesteten
		* Geraeten nicht notwendig, da diese auch bei USB-Sticks und externen
		* SD-Karten die ACTION_MEDIA-Intents abgefeuert haben
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		*/
		return filter;
	}
	/**
	* Schaltet die Benutzung des BroadcastReceivers ein oder aus. Wenn sie eingeschaltet ist, muss
	* man daran denken, bei App-Ende setUseReceiver(context, false) aufzurufen.
	*
	* @param context der Context der App
	* @param use true schaltet den BroadcastReceiver sofort ein, false schaltet ihn aus und
	* ruft auch Context.unregisterReceiver auf.
	*/
	public static void setUseReceiver(Context context, boolean use) 
	{
		if (use && receiver == null) 
		{
			// receiver einschalten
			receiver = new BroadcastReceiver() 
			{
				@Override
				public void onReceive(Context context, Intent intent) 
				{
					Log.i(TAG, "Storage " + intent.getAction() + "-" + intent.getData());
					initDevices(context);
				}
			};
			context.registerReceiver(receiver, getRescanIntentFilter());
		} 
		else if (!use && receiver != null) 
		{
			// receiver ausschalten
			context.unregisterReceiver(receiver);
			receiver = null;
		}
	}
	/**
	* Die Device-Liste wird erstellt. Kann manuell aufgerufen werden, wird aber auch
	* automatisch beim ersten Aufruf von {@link #getDevices(Context)}
	* aufgerufen. Liefert kein Ergebnis zurueck, die Liste muss dann auch darueber
	* abgefragt werden.
	*
	* @param context Der Context der App
	*/
	private static void initDevices(Context context)
	{
		// Userverzeichnis
		if (userDir ==null) 
			userDir = "/Android/data/" + context.getPackageName();
			// Broadcast-Receiver
		//setUseReceiver(context, useReceiver);
		// Devices einlesen
		StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		Class c = sm.getClass();
		Object[] vols;
		try {
			// Aufruf der versteckten Methode getVolumeList
			//noinspection NullArgumentToVariableArgMethod
			Method m = c.getMethod("getVolumeList", null);
			//noinspection NullArgumentToVariableArgMethod
			vols = (Object[]) m.invoke(sm, null); // android.os.Storage.StorageVolume
			Device[] temp = new Device[vols.length];
			for (int i = 0; i < vols.length; i++) 
				temp[i] = new Device(vols[i]);
				// isPrimary unter Android 4.2 reparieren: Wenn kein Eintrag isPrimary
				// hat, dann suche den ersten Eintrag, der nicht isRemovable ist. Wenn
				// auch dann nix gefunden wurde (seltsam...), dann setze einfach den
				// ersten Eintrag auf isPrimary
			Device primary = null;
			for (Device d : temp) 
				if (d.mPrimary) 
					primary = d;
				if (primary==null) 
					for (Device d : temp)
						if (!d.mRemovable) 
						{
							d.mPrimary = true;
							primary = d;
							break;
						}
						if (primary==null) 
						{
							primary = temp[0];
							primary.mPrimary = true;
						}
						// ExternalDirs setzen (nur das erzeugt diese Pfade unter Android ab 4.4)
				File[] files = ContextCompat.getExternalFilesDirs(context,null);
				File[] caches = ContextCompat.getExternalCacheDirs(context);
	
				for (Device d : temp) 
				{
					if (files!=null) for (File f : files)
					if (f!=null && f.getAbsolutePath().startsWith(d.getAbsolutePath())) d.mFiles = f;
					if (caches!=null) for (File f : caches)
					if (f!=null && f.getAbsolutePath().startsWith(d.getAbsolutePath())) d.mCache = f;
				}
				// die drei Listen erzeugen
				ArrayList<Device> tempDev = new ArrayList<>(10);
				ArrayList<Device> tempStor = new ArrayList<>(10);
				ArrayList<Device> tempExt = new ArrayList<>(10);
				for (Device d : temp) 
				{
					tempDev.add(d);
					if (d.isAvailable()) 
						{
							tempExt.add(d); tempStor.add(d);
						}
				}
				// internen Speicher (pre-unified) einbinden -- tritt hauptsaechlich bei Geraeten auf,
				// die mit Android 2.x ausgeliefert wurden und auch nach einem Update auf 4.x
				// noch die alte Partitionierung haben.
				Device internal = new Device(context);
				tempStor.add(0, internal); // bei Storage-Alternativen immer
				if (!primary.mEmulated) 
					tempDev.add(0, internal); // bei Devices nur wenn zusaetzlich
					// temp in devices-Tabelle uebernehmen
					devices = tempDev.toArray(new Device[tempDev.size()]);
					storage = tempStor.toArray(new Device[tempStor.size()]);
					externalstorage= tempExt.toArray(new Device[tempExt.size()]);
		} 
		catch (Exception e) 
		{
			// Fallback auf normale Android-Funktionen
			Log.e(TAG, "getVolumeList not found, fallback");
			//  ist noch bei keinem Testgeraet vorgekommen
		}
	}
	/**
	* Device erweitert File um Methoden zum Umgang mit externen Speichermedien; teils sind
	* sie von der versteckten Klasse StorageVolume uebernommen. Um weche Art von Speicher
	* es sich handelt, verraet {@link #getType()}. Ob das Device eingesteckt ist oder nur einen
	* leeren Slot beschreibt, sagt {@link #isAvailable()}.
	* <p>
	* Device kann wie ein File verwendet werden, z.B. funktionieren {@link #getFreeSpace()}
	* und {@link #getTotalSpace()}. Allerdings hat {@link #canWrite()} keine
	* Aussagekraft, sondern die komplizierteren Zugriffsmoeglichkeiten v.a. ab Android 4.4
	* erfaehrt man per {@link #getAccess()}.
	* <p>
	* Wenn man nur WRITE_APPONLY gewaehrt bekommt, liefern {@link #getFilesDir()} und
	* {@link #getCacheDir()} die Verzeichnisse, auf die man schreiben darf.
	*
	* @see <a href="http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.4_r1/android/os/storage/StorageVolume.java?av=f">
	* android.os.storage.StorageVolume</a>
	*/
	public static class Device extends File 
	{
		String mUserLabel, mUuid, mState, mWriteState, mType;
		boolean mPrimary, mRemovable, mEmulated, mAllowMassStorage;
		long mMaxFileSize;
		File mFiles, mCache;
		/**
		* Erzeugen aus context.getDataDirectory(), also interner Speicher
		* @param context der Context der App
		*/
		Device(Context context) 
		{
		super(Environment.getDataDirectory().getAbsolutePath());
			mState = Environment.MEDIA_MOUNTED;
			mFiles = context.getFilesDir();
			mCache = context.getCacheDir();
			mType = TYPE_INTERNAL;
			mWriteState = WRITE_APPONLY;
		}
		/**
		* Einlesen aus einem StorageVolume per Reflection
		* @param storage
		* @throws NoSuchMethodException
		* @throws InvocationTargetException
		* @throws IllegalAccessException
		*/
		@SuppressWarnings("NullArgumentToVariableArgMethod")
		Device(Object storage) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException 
		{
			super((String)storage.getClass().getMethod("getPath",null).invoke(storage, null));
			for (Method m : storage.getClass().getMethods()) 
			{
				if (m.getName().equals("getUserLabel") && m.getParameterTypes().length == 0 && m.getReturnType() == String.class)
					mUserLabel = (String) m.invoke(storage, null); // ab Android 4.4
				if (m.getName().equals("getUuid") && m.getParameterTypes().length == 0 && m.getReturnType() == String.class)
					mUuid = (String) m.invoke(storage, null); // ab Android 4.4
				if (m.getName().equals("getState") && m.getParameterTypes().length == 0 && m.getReturnType() == String.class)
					mState = (String) m.invoke(storage, null); // ab Android 4.4
				if (m.getName().equals("isRemovable") && m.getParameterTypes().length == 0 && m.getReturnType() == boolean.class)
					mRemovable = (Boolean) m.invoke(storage, null); // ab Android 4.0
				if (m.getName().equals("isPrimary") && m.getParameterTypes().length == 0 && m.getReturnType() == boolean.class)
					mPrimary = (Boolean) m.invoke(storage, null); // ab Android 4.2
				if (m.getName().equals("isEmulated") && m.getParameterTypes().length == 0 && m.getReturnType() == boolean.class)
					mEmulated = (Boolean) m.invoke(storage, null); // ab Android 4.0
				if (m.getName().equals("allowMassStorage") && m.getParameterTypes().length == 0 && m.getReturnType() == boolean.class)
					mAllowMassStorage = (Boolean) m.invoke(storage, null); // ab Android 4.0
				if (m.getName().equals("getMaxFileSize") && m.getParameterTypes().length == 0 && m.getReturnType() == long.class)
					mMaxFileSize = (Long) m.invoke(storage, null); // ab Android 4.0
				// getDescription (ab 4.1 mit context) liefert keine sinnvollen Werte
				// getPathFile (ab 4.2) liefert keine sinnvollen Werte
				// getMtpReserveSpace (ab 4.0) fuer diese Zwecke unwichtig
				// getStorageId (ab 4.0) fuer diese Zwecke unwichtig
			}
			if (mState==null) 
				mState = getState();
			if (mPrimary)
				mType = TYPE_PRIMARY;
			else 
			{
				String n = getAbsolutePath().toLowerCase();
				if (n.indexOf("sd")>0)
					mType = TYPE_SD;
				else if (n.indexOf("usb")>0)
					mType = TYPE_USB;
				else
					mType = TYPE_UNKNOWN + " " + getAbsolutePath();
			}
		}
		/**
		* Liefert den Typ des Geraets zurueck
		*
		* @return ein String mit TYPE_PRIMARY = Festspeicher, TYPE_SD = SD-Karte (auch
		* im Akkuschacht sitzende), TYPE_USB = USB-Speicher, TYPE_INTERNAL =
		* die bis Android 2.x vorhandene /data-Partition, TYPE_UNKNOWN bei sonstigen,
		* wozu wohl auch speziell verschluesselte Bereiche zaehlen; habe ich bisher nur
		* bei einem Note4 und Galaxy S5 mal gesehen und noch nicht weiter untersucht.
		*/
		public String getType() 
		{
			return mType;
		}
		/**
		* Liefert die Zugriffsart zurueck. Bis Android 4.3 hatte man Vollzugriff auf alles, doch
		* ab 4.4 gilt das fuer SD-Karten und USB-Speicher nicht mehr -- da duerfen Apps zwar
		* alles lesen, aber nur noch auf ihr privates Verzeichnis schreiben. Inkonsequenterweise
		* erlaubt Google weiterhin den vollen Schreibzugriff auf den internen Geraetespeicher.
		*
		* @return ein String, der die Zugriffsart beschreibt:
		* <ul>
		* <li>WRITE_NONE = nichtmal Lesezugriff, duerfte nur bei nicht vorhandenen
		* Devices passieren.</li>
		* <li>WRITE_READONLY = Device als readonly gemounted, ist ungetestet, weil
		* mir das noch nicht untergekommen ist.</li>
		* <li>WRITE_APPONLY = Android-4.4-Einschraenkung, Schreibzugriff nur aufs
		* eigene Verzeichnis. Das kriegt man dann ueber {@link #getCacheDir()} und
		* {@link #getFilesDir()} geliefert. Achja, eine etwaige alte /data-Partition hat
		* auch diese Access-Art, bzw. ist man da sogar noch eingeschraenkter, da man
		* die Verzeichnisse anderer Apps nicht lesen darf. Das ueberpruefe ich hier
		* allerdings nicht differenziert.</li>
		* <li>WRITE_FULL = Schreibzugriff in allen Verzeichnissen</li>
		* </ul>
		*/
		public String getAccess() 
		{
			if (mWriteState == null) 
			{
				try 
				{
					mWriteState = WRITE_NONE;
					File[] root = listFiles();
					if (root==null || root.length==0) 
						throw new IOException("root empty/unreadable");
					mWriteState = WRITE_READONLY;
					File t = File.createTempFile("jow", null, getFilesDir());
					//noinspection ResultOfMethodCallIgnored
					t.delete();
					mWriteState = WRITE_APPONLY;
					t = File.createTempFile("jow", null, this);
					//noinspection ResultOfMethodCallIgnored
					t.delete();
					mWriteState = WRITE_FULL;
				} 
				catch (IOException ignore) 
				{
					Log.v(TAG, "test "+getAbsolutePath()+" ->"+mWriteState+"<- "+ignore.getMessage());
				}
			}
			return mWriteState;
		}
		/**
		* Ueberprueft anhand {@link #getState()}, ob das Device vorhanden ist. Garantiert
		* noch keinen Schreibzugriff, das kann man per {@link #getAccess()} erfahren.
		*
		* @return true, wenn Device vorhanden ist, im Allgemeinen hat man dann auch
		* Lesezugriff. Schreibzugriff nicht unbedingt. false, wenn Device fehlt.
		*/
		public boolean isAvailable() 
		{
			String s = getState();
			return (Environment.MEDIA_MOUNTED.equals(s) ||	Environment.MEDIA_MOUNTED_READ_ONLY.equals(s));
			// MEDIA_SHARED: als USB freigegeben; bitte Handy auf MTP umstellen
		}
		/**
		* Ueberprueft, ob das Device vorhanden ist. Wenn nur ja/nein gefordert ist, liefert das
		* {@link #isAvailable()} einfacher.
		* <p>
		* Ab Android 4.4 wird das an {@link Environment#getStorageState(File)}}
		* deligiert, ansonsten wird ueberprueft, ob das Verzeichnis lesbar ist und ob die Groesse
		* groesser Null ist.
		*
		* @return ein String wie in {@link Environment} definiert. Im Allgemeinen
		* bedeuten nur MEDIA_MOUNTED und MEDIA_MOUNTED_READ_ONLY, dass das
		* Device wirklich funktioniert. Die anderen sind mehr oder weniger nur Fehlerhinweise,
		* z.B. MEDIA_SHARED = ist per USB an einen PC weitergereicht und kann daher nicht
		* gelesen werden.
		*/
		@SuppressWarnings("deprecation")
		public String getState() 
		{
			if (mRemovable || mState==null) 
			{
				// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				// Android 5.0? Da gibts was neues
				// mState = Environment.getExternalStorageState(this);
				// else 
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
					// Android 4.4? Dann dort nachfragen
					mState = Environment.getStorageState(this);
				else
					mState = EnvironmentCompat.getStorageState(this);
				/*
				else if (canRead() && getTotalSpace() > 0)
					// lesbar und Groesse vorhanden => gibt es
					mState = Environment.MEDIA_MOUNTED;
				else if (mState==null || Environment.MEDIA_MOUNTED.equals(mState))
					// nicht lesbar, keine Groesse aber noch MOUNTED || oder ungesetzt => UNKNOWN
					mState = EnvironmentCompat.MEDIA_UNKNOWN;
				*/	
			}
		return mState;
		}
		
		public File getFilesDir() 
		{
			if (mFiles == null) 
			{
				mFiles = new File(this, userDir + "/files");
				if (!mFiles.isDirectory())
					//noinspection ResultOfMethodCallIgnored
					mFiles.mkdirs();
			}
		return mFiles;
		}
		
		public File getCacheDir() 
		{
			if (mCache==null) 
			{
				mCache = new File(this, userDir + "/cache");
				if (!mCache.isDirectory())
					//noinspection ResultOfMethodCallIgnored
					mCache.mkdirs();
			}
			return mCache;
		}
		/**
		* Liefert zurueck, ob das der primaere Speicher ist. Das ist der, den man auch bei
		* {@link Context#getExternalFilesDir(String)} bekommt.
		*
		* @return true beim primaeren externen Speicher, false bei weiteren externen
		* Speichern und bei einer etwaigen /data-Partition
		*/
		public boolean isPrimary() 
		{ 
			return mPrimary; 
		}
		/**
		* Untersucht, ob das Device im Betrieb entnommen werden kann.
		*
		* @return true, wenn der Nutzer das Device jederzeit herausnehmen kann. False,
		* falls fest eingebaut oder nur nach Ausschalten entnehmbar. True bedeutet
		* also nicht, dass das Device auch wirklich vorhanden ist, sondern das bekommt
		* man auch fuer SD-Slots im Akkuschacht.
		*/
		public boolean isRemovable() 
		{ 
			return mRemovable; 
		}
		/**
		* Liefert zurueck, ob es sich um einen unified Memory handelt. Das war eine mit
		* Android 3 eingefuehrte Speichertechnik. Hat nur interne Bedeutung.
		*
		* @return true fuer moderne Geraete mit unified Memory, bei denen sich die /data-
		* Partition und der primaere Speicher eine echte Partition teilen. False bei aelteren
		* Geraeten; bedeutet, dass die /data-Partition auch noch vorhanden ist und von
		* {@link #getDevices(Context)} am Index 1 geliefert wird.
		*/
		public boolean isEmulated() 
		{ 
			return mEmulated; 
		}
		public boolean isAllowMassStorage() 
		{ 
			return mAllowMassStorage; 
		}
		public long getMaxFileSize() 
		{ 
			return mMaxFileSize; 
		}
		public String getUserLabel() 
		{ 
			return mUserLabel; 
		}
		public String getUuid() 
		{ 
			return mUuid; 
			
		}
	}
}
