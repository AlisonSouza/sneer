; this file should be saved in iso8859-1 format (second button->properties)

!macro LANGUAGE_CONFIG

	!insertmacro MUI_LANGUAGE "English"
	!insertmacro MUI_LANGUAGE "PortugueseBR"
  
	LangString STRING_SNEERCONFIGURATION ${LANG_ENGLISH} "Sneer Configuration will now be executed,$\r$\nyou are not obligated to do this now,$\r$\nyou can do it later if you want."
	LangString STRING_SNEERCONFIGURATION ${LANG_PortugueseBR} "A configura��o do Sneer ser� executada,$\r$\nvoc� n�o � obrigado a fazer isto agora,$\r$\npode execut�-la mais tarde."
	
	LangString STRING_JAVANOTINSTALLED ${LANG_ENGLISH} "Java 1.6 is not installed! Download it from http://www.java.com ." 
	LangString STRING_JAVANOTINSTALLED ${LANG_PortugueseBR} "O Java 1.6 n�o est� instalado! Fa�a o download de http://www.java.com .</htm>" 
	
	LangString STRING_JAVAWILLBEDOWNLOADED ${LANG_ENGLISH} "Sneer uses Java 1.6, it will now be downloaded and installed."
	LangString STRING_JAVAWILLBEDOWNLOADED ${LANG_PortugueseBR} "O Sneer utiliza o Java 1.6, ele ser� baixado e executado agora."

	LangString STRING_JREINSTALLWILLTAKESOMETIME ${LANG_ENGLISH} "Please wait a few minutes while java 1.6 is being installed."
	LangString STRING_JREINSTALLWILLTAKESOMETIME ${LANG_PortugueseBR} "Por favor espere alguns minutos enquanto o java 1.6 � instalado."

!macroend