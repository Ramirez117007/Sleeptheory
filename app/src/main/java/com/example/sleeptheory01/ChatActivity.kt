package com.example.sleeptheory01

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 100
    }

    private lateinit var userInput: EditText
    private lateinit var voiceButton: ImageButton
    private lateinit var sendButton: ImageButton
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val messageList = ArrayList<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Inicializa los elementos de la UI
        userInput = findViewById(R.id.userInput)
        voiceButton = findViewById(R.id.voiceButton)
        sendButton = findViewById(R.id.sendButton)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)

        // Configura el RecyclerView
        chatAdapter = ChatAdapter(messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        // Listeners de los botones
        sendButton.setOnClickListener { sendMessage() }
        voiceButton.setOnClickListener { startVoiceRecognition() }

        // Manejo de teclado para mover el inputLayout
        val mainLayout = findViewById<View>(R.id.main)
        val inputLayout = findViewById<LinearLayout>(R.id.ll_input)

        mainLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            mainLayout.getWindowVisibleDisplayFrame(rect)
            val screenHeight = mainLayout.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.50) {
                inputLayout.translationY = -keypadHeight.toFloat()
            } else {
                inputLayout.translationY = 0f
            }
        }
    }

    private fun sendMessage() {
        val text = userInput.text.toString().trim()
        if (text.isNotEmpty()) {
            // Agrega el mensaje del usuario al chat
            messageList.add(Message(text, Message.SENDER_USER))
            chatAdapter.notifyItemInserted(messageList.size - 1)
            chatRecyclerView.scrollToPosition(messageList.size - 1)

            // Obtiene la respuesta del chatbot
            val botResponse = getBotResponse(text)
            messageList.add(Message(botResponse, Message.SENDER_BOT))
            chatAdapter.notifyItemInserted(messageList.size - 1)
            chatRecyclerView.scrollToPosition(messageList.size - 1)

            userInput.text.clear()
        }
    }

    private fun getBotResponse(userMessage: String): String {
        return when {
            "hola" in userMessage.lowercase() -> "¡Hola! ¿Cómo puedo ayudarte?"
            "buenos dias" in userMessage.lowercase() -> "¡Buenos días! ¿Cómo puedo ayudarte hoy?"
            "buenas tardes" in userMessage.lowercase() -> "¡Buenas tardes! ¿Cómo puedo ayudarte?"
            "buenas noches" in userMessage.lowercase() -> "¡Buenas noches! ¿Cómo puedo ayudarte?"
            "aa" in userMessage.lowercase() -> "¡Parece que algo te ha frustrado! ¿Te gustaría contarme qué pasa? Estoy aquí para ayudarte."
            "qué onda" in userMessage.lowercase() -> "¿Que onda? ¿Cómo puedo asistirte hoy?"
            "qué haces" in userMessage.lowercase() -> "Estoy aquí para ayudarte con lo que necesites. ¿Cómo puedo asistirte hoy?"
            "tengo hambre" in userMessage.lowercase() -> "Es hora de comer algo. ¿Qué tal una comida saludable o tu snack favorito?"
            "qué día es hoy" in userMessage.lowercase() -> {
                val formatter = DateTimeFormatter.ofPattern("EEEE", Locale("es", "ES"))
                val today = LocalDate.now().format(formatter).capitalize()
                "Hoy es $today, ¡un buen día para hacer algo especial!"
            }
            "hace frío" in userMessage.lowercase() -> "Abrígate bien y, si puedes, prepárate algo calentito como un té o una sopa."
            "quiero viajar" in userMessage.lowercase() -> "Viajar es una gran idea. ¿Tienes algún lugar en mente o necesitas inspiración?"
            "qué puedo cocinar" in userMessage.lowercase() -> "Depende de lo que tengas en casa. ¿Qué tal una receta fácil como pasta, una ensalada o algo al horno?"
            "me voy a duchar" in userMessage.lowercase() -> "¡Perfecto! Una ducha siempre ayuda a sentirse renovado. 🌟"
            "vi algo gracioso" in userMessage.lowercase() -> "¡Qué genial! Compartirlo con alguien podría alegrarles el día también."
            "necesito un consejo" in userMessage.lowercase() -> "Estoy aquí para ayudarte. ¿De qué se trata? Hablemos."
            "no sé qué hacer" in userMessage.lowercase() -> "A veces es bueno simplemente relajarse o probar algo nuevo. ¿Qué tal escuchar música o caminar?"
            "me duele la cabeza" in userMessage.lowercase() -> "Lo siento. Toma un descanso, hidrátate y, si es posible, relájate en un lugar tranquilo."
            "hoy estoy feliz" in userMessage.lowercase() -> "¡Qué lindo escuchar eso! Disfruta tu día al máximo y comparte tu alegría con los demás."
            "se me olvidó algo" in userMessage.lowercase() -> "¡No te preocupes! Trata de recordar los pasos que seguiste, seguro te viene a la mente pronto."
            "vi una película" in userMessage.lowercase() -> "¡Genial! ¿Qué película viste? ¿La recomendarías?"
            "no quiero hacer nada" in userMessage.lowercase() -> "Está bien tomarse un descanso. Aprovecha para recargar energías y relajarte."
            "se me rompió algo" in userMessage.lowercase() -> "¡Qué mal! A veces se puede reparar. Si no, quizá sea la oportunidad de buscar algo nuevo."
            "tengo que estudiar" in userMessage.lowercase() -> "¡Tú puedes! Divide el estudio en partes pequeñas y tómate descansos para mantenerte enfocado."
            "salí a caminar" in userMessage.lowercase() -> "¡Qué bien! Caminar es excelente para despejar la mente y mantenerse activo."
            "escuché una canción" in userMessage.lowercase() -> "¡La música siempre alegra el día! ¿Qué canción escuchaste? Tal vez la añada a mi lista de favoritos."
            "quiero aprender algo nuevo" in userMessage.lowercase() -> "¡Eso es fantástico! ¿Te interesa algo creativo, técnico o un nuevo idioma?"
            "se me perdió algo" in userMessage.lowercase() -> "¡Qué frustrante! Intenta buscar en lugares donde lo uses frecuentemente. Seguro aparecerá pronto."
            "necesito inspiración" in userMessage.lowercase() -> "La inspiración está en todas partes: lee algo, escucha música o simplemente observa el mundo a tu alrededor."
            "qué hora es" in userMessage.lowercase() -> {
                val now = LocalTime.now()
                val formattedTime = String.format("%02d:%02d", now.hour, now.minute)
                "Ahora son las $formattedTime. ¿Planeas algo interesante?"
            }
            "me siento triste" in userMessage.lowercase() -> "Lamento que te sientas así. A veces hablar con alguien o hacer algo que disfrutes puede ayudarte. Estoy aquí si necesitas desahogarte."
            "estoy feliz" in userMessage.lowercase() -> "¡Qué alegría escucharlo! Disfruta este momento y compártelo con quienes te rodean."
            "me siento solo" in userMessage.lowercase() -> "La soledad puede ser difícil. Intenta conectarte con un amigo o realizar una actividad que te guste para sentirte mejor."
            "estoy enojado" in userMessage.lowercase() -> "El enojo puede ser abrumador. Tómate un momento para respirar profundo, relájate y reflexiona sobre lo que te molesta."
            "estoy ansioso" in userMessage.lowercase() -> "La ansiedad puede ser intensa. Intenta respirar lentamente, enfócate en el presente o escucha una meditación guiada."
            "me siento frustrado" in userMessage.lowercase() -> "Es normal sentirse frustrado a veces. Tómate un momento para reflexionar y busca una solución paso a paso."
            "me siento agradecido" in userMessage.lowercase() -> "¡Eso es hermoso! La gratitud es poderosa. ¿Por qué no escribes lo que agradeces hoy para recordarlo?"
            "estoy motivado" in userMessage.lowercase() -> "¡Eso es increíble! Aprovecha este momento para avanzar en tus metas y mantener esa energía positiva."
            "estoy agotado" in userMessage.lowercase() -> "Es importante descansar. Intenta desconectarte un rato y recargar energías con algo que disfrutes."
            "me siento aburrido" in userMessage.lowercase() -> "El aburrimiento puede ser una oportunidad para probar algo nuevo. ¿Qué tal un pasatiempo, ejercicio o aprender algo interesante?"
            "me siento amado" in userMessage.lowercase() -> "¡Qué lindo sentimiento! Disfruta de ese amor y compártelo con los demás."
            "me siento incomprendido" in userMessage.lowercase() -> "Lamento que te sientas así. A veces ayuda expresar lo que sientes con calma a alguien que pueda escucharte."
            "me siento perdido" in userMessage.lowercase() -> "Sentirse perdido es normal a veces. Reflexiona sobre lo que realmente te importa y da un pequeño paso hacia eso."
            "tengo miedo" in userMessage.lowercase() -> "Es natural sentir miedo. Respira profundamente, identifica lo que lo causa y busca apoyo si lo necesitas."
            "me siento tranquilo" in userMessage.lowercase() -> "La tranquilidad es maravillosa. Disfruta el momento y recarga energías."
            "me siento nervioso" in userMessage.lowercase() -> "Los nervios pueden ser desafiantes. Respira profundamente y recuerda que todo pasará."
            "estoy satisfecho" in userMessage.lowercase() -> "¡Eso es genial! La satisfacción es un reflejo de tus logros y esfuerzos."
            "me siento culpable" in userMessage.lowercase() -> "La culpa puede ser difícil de manejar. Reflexiona sobre lo que sientes y, si es posible, busca una manera de enmendar o perdonarte."
            "me siento esperanzado" in userMessage.lowercase() -> "La esperanza es un motor poderoso. Sigue adelante y mantén esa actitud positiva."
            "estoy cansado" in userMessage.lowercase() -> "A veces necesitamos un momento para desconectar. Intenta tomar un breve descanso, respirar profundo y recargar energías."
            "tengo mucho trabajo" in userMessage.lowercase() -> "Recuerda priorizar y tomar pausas. Organiza tus tareas por importancia y no olvides cuidar de ti mismo."
            "no sé qué hacer hoy" in userMessage.lowercase() -> "Es un buen momento para probar algo nuevo. ¿Qué tal leer, caminar o explorar un pasatiempo que te interese?"
            "hoy no tengo energía" in userMessage.lowercase() -> "¡Está bien tomarte un día para descansar! Escucha a tu cuerpo y permítete recargar fuerzas."
            "me siento abrumado" in userMessage.lowercase() -> "Tómate un momento para respirar. Divide tus tareas en partes más pequeñas y aborda una cosa a la vez."
            "necesito motivación" in userMessage.lowercase() -> "Piensa en pequeños pasos que te acerquen a tu meta. ¡Cada logro cuenta y estás más cerca de lo que crees!"
            "quiero hacer ejercicio" in userMessage.lowercase() -> "¡Genial! Empieza con algo que disfrutes, como caminar o bailar. Lo importante es moverte y sentirte bien."
            "qué hago si estoy aburrido" in userMessage.lowercase() -> "Prueba algo creativo, como dibujar o escribir, o haz una actividad física para despejar la mente."
            "necesito un consejo" in userMessage.lowercase() -> "Estoy aquí para ayudarte. Cuéntame más y veamos cómo podemos resolverlo juntos."
            "cómo tener un mejor día" in userMessage.lowercase() -> "Empieza con algo positivo: escucha música que te guste, organiza tus prioridades y toma pequeños momentos para ti."
            "quiero ser más productivo" in userMessage.lowercase() -> "Crea una lista de tareas realista, prioriza lo más importante y elimina distracciones como el móvil."
            "estoy feliz hoy" in userMessage.lowercase() -> "¡Eso es maravilloso! Disfruta de este día y comparte tu alegría con los demás."
            "tengo hambre" in userMessage.lowercase() -> "Es hora de recargar energías. ¿Qué tal algo saludable y rápido para comer?"
            "no tengo ganas de nada" in userMessage.lowercase() -> "Todos tenemos días así. Intenta hacer algo pequeño que te guste o simplemente descansa y recupérate."
            "me siento solo" in userMessage.lowercase() -> "Hablar con un amigo o familiar puede ayudar. También puedes salir a caminar o unirte a una actividad grupal."
            "necesito desconectar" in userMessage.lowercase() -> "Intenta dejar el teléfono por un rato y hacer algo relajante, como leer o escuchar música tranquila."
            "me cuesta organizarme" in userMessage.lowercase() -> "Empieza por anotar tus pendientes. Divide tus tareas grandes en pasos pequeños y usa un calendario o lista de prioridades."
            "qué puedo hacer divertido" in userMessage.lowercase() -> "¿Qué tal jugar un juego, ver una película cómica o probar algo nuevo como cocinar una receta diferente?"
            "cómo relajarse" in userMessage.lowercase() -> "Busca un lugar tranquilo, respira profundamente y prueba técnicas como escuchar música relajante o meditar por unos minutos."
            "tengo insomnio" in userMessage.lowercase() -> "El insomnio puede ser frustrante. Intenta mantener una rutina de sueño constante y evita las pantallas al menos 30 minutos antes de acostarte."
            "me cuesta dormir" in userMessage.lowercase() -> "Entiendo que dormir puede ser difícil a veces. Intenta técnicas como la respiración 4-7-8 o escucha sonidos relajantes."
            "no puedo conciliar el sueño" in userMessage.lowercase() -> "Si no puedes conciliar el sueño, intenta leer algo ligero o practicar una meditación guiada para relajarte."
            "me despierto en la noche" in userMessage.lowercase() -> "Despertarse durante la noche puede ser normal, pero si ocurre a menudo, considera reducir el estrés antes de dormir y limitar líquidos antes de acostarte."
            "por qué tengo insomnio" in userMessage.lowercase() -> "El insomnio puede deberse a muchas causas, como estrés, ansiedad, o hábitos de sueño poco saludables. ¿Quieres algunos consejos para mejorar tu sueño?"
            "cómo superar el insomnio" in userMessage.lowercase() -> "Superar el insomnio requiere paciencia. Prueba a establecer horarios regulares, crear un ambiente relajante y evitar comidas pesadas antes de dormir."
            "qué es el insomnio" in userMessage.lowercase() -> "El insomnio es un trastorno del sueño que dificulta conciliar o mantener el sueño, afectando tu descanso y energía durante el día."
            "remedios para dormir" in userMessage.lowercase() -> "Algunos remedios naturales incluyen tomar té de manzanilla, practicar yoga suave antes de dormir, o usar aceites esenciales como lavanda."
            "me siento cansado pero no duermo" in userMessage.lowercase() -> "Sentirse cansado pero no poder dormir puede ser señal de estrés. Intenta desconectarte gradualmente y crea una rutina relajante antes de acostarte."
            "tips para dormir mejor" in userMessage.lowercase() -> "Algunos tips para dormir mejor incluyen mantener tu habitación fresca, usar una luz tenue antes de acostarte y practicar ejercicios de relajación como mindfulness."
            "la meditación ayuda con el insomnio" in userMessage.lowercase() -> "¡Sí! La meditación puede ayudar a calmar tu mente y prepararte para un sueño más reparador. ¿Te gustaría probar una guía rápida de meditación?"
            "por qué no puedo dormir" in userMessage.lowercase() -> "No dormir puede tener muchas causas, como preocupaciones, uso de dispositivos electrónicos o café antes de acostarte. ¿Te gustaría explorar soluciones juntos?"
            "desperté a las 3 am" in userMessage.lowercase() -> "Despertar en medio de la noche puede ser causado por estrés o un ambiente poco óptimo. Trata de relajarte, evita mirar el reloj y respira profundamente."
            "cuánto debo dormir" in userMessage.lowercase() -> "La mayoría de los adultos necesitan entre 7 y 9 horas de sueño por noche. Escucha a tu cuerpo y ajusta tu rutina según tus necesidades."
            "mi mente no se calma por la noche" in userMessage.lowercase() -> "Si tu mente no se calma, intenta escribir tus pensamientos en un diario antes de dormir para liberar tus preocupaciones."
            "cómo calmar mi mente" in userMessage.lowercase() -> "Calmar tu mente puede lograrse con respiración profunda, meditación, o escuchando sonidos relajantes. ¿Te gustaría probar alguna técnica?"
            "no puedo dormir" in userMessage.lowercase() -> "Lo siento que estés teniendo problemas para dormir. Intenta establecer una rutina de sueño regular, evita el consumo de cafeína antes de dormir y procura que tu habitación esté oscura y tranquila. También puede ser útil practicar técnicas de relajación como la respiración profunda o la meditación."
            "adiós" in userMessage.lowercase() -> "¡Hasta luego! Cuídate."
            "cómo estás" in userMessage.lowercase() -> "Estoy aquí para ayudarte. 😊"
            else -> "Lo siento, no entiendo eso. ¿Puedes reformularlo?"
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, "Tu dispositivo no admite reconocimiento de voz", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!result.isNullOrEmpty()) {
                val transcribedText = result[0]
                userInput.setText(transcribedText)
                sendMessage() // Envía automáticamente el texto transcrito
            }
        }
    }
}