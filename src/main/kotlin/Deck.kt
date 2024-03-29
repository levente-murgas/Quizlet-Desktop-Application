import java.io.File
import kotlin.random.Random

class Deck(){
    private var flashcards : MutableMap<String, String> = mutableMapOf()
    private var cardsAndErrors : MutableMap<Pair<String, String>, Int> = mutableMapOf()

    fun <K, V> getKey(map: Map<K, V>, target: V): K? {
        for ((key, value) in map)
        {
            if (target == value) {
                return key
            }
        }
        return null
    }

    fun addCard(){
        logger.logOutput("Card :")
        var newTerm = logger.logInput()
        if(flashcards.containsKey(newTerm)) {
            var unique = false
            while (!unique) {
                logger.logOutput("The card \"$newTerm\" already exists.")
                logger.logOutput("Card :")
                newTerm = logger.logInput()
                if(!flashcards.containsKey(newTerm))
                    unique = true

            }
        }
        logger.logOutput("The definition of the card:")
        var newDefinition = logger.logInput()
        if(flashcards.containsValue(newDefinition)){
            var unique = false
            while (!unique) {
                logger.logOutput("The definition \"$newDefinition\" already exists.")
                logger.logOutput("The definition of the card:")
                newDefinition = logger.logInput()
                if(!flashcards.containsValue(newDefinition))
                    unique = true

            }
        }

        flashcards[newTerm] = newDefinition
        cardsAndErrors[Pair(newTerm, flashcards[newTerm]!!)] = 0
        logger.logOutput("The pair (\"$newTerm\":\"$newDefinition\") has been added.")
    }

    fun removeCard() {
        logger.logOutput("Which card?")
        val term = logger.logInput()
        if(flashcards.containsKey(term)){
            cardsAndErrors.remove(Pair(term, flashcards[term]))
            flashcards -= term
            logger.logOutput("The card has been removed.")
        }
        else{
            logger.logOutput("Can't remove \"$term\": there is no such card.")
        }
    }

    fun importCards(importFileName: String = ""){
        var fileName = importFileName
        if(importFileName.isEmpty()) {
            logger.logOutput("File name:")
            fileName = logger.logInput()
        }
        val file = File(fileName)
        if(file.exists()){
            val lines = file.readLines()
            for(line in lines){
                var (originalCard,errors) = line.split("=") //(term, definition)=1
                var filteredCard = originalCard.filter { c -> c != '(' && c != ')' && c != ','} //term definition
                var (term, definition) = filteredCard.split(' ')
                if(flashcards.containsKey(term)){
                    cardsAndErrors.remove(Pair(term, flashcards[term]))
                    cardsAndErrors[Pair(term,definition)] = errors.toInt()
                    flashcards.replace(term,definition)
                }
                else{
                    flashcards[term] = definition
                    cardsAndErrors[Pair(term, flashcards[term]!!)] = errors.toInt()
                }
            }
            logger.logOutput("${lines.size} cards have been loaded.")

        } else logger.logOutput("File not found.")

    }

    fun exportCards(exiting: Boolean = false, exportFileName : String = ""){
        var fileName = exportFileName
        if(exportFileName.isEmpty() && !exiting) {
            logger.logOutput("File name:")
            fileName = logger.logInput()
        }
        val file = File(fileName)
        //val cards = flashcards.entries.joinToString(separator = "\n")
        val cardsAndErrors = cardsAndErrors.entries.joinToString(separator = "\n")
        if(file.exists() || fileName.isNotEmpty()) {
            file.writeText(cardsAndErrors)
            logger.logOutput("${flashcards.size} cards have been saved.")
        }
    }

    fun askCard(){
        logger.logOutput("How many times to ask?")
        val numOfCards = logger.logInput().toInt()
        for(i in 1..numOfCards){
            val random = Random.nextInt(0, flashcards.size)
            var index = 0
            for (entry in flashcards) {
                if (index == random){
                    logger.logOutput("Print the definition of \"${entry.key}\":")
                    var answer = logger.logInput()
                    if(answer == entry.value){
                        logger.logOutput("Correct!")
                    }
                    else{
                        logger.logOutput("Wrong. The right answer is \"${entry.value}\", " +
                                "but your definition is correct for \"${getKey(flashcards,answer)}\".")
                        val newErrorCount = cardsAndErrors[Pair(entry.key,entry.value)]?.plus(1)
                        if (newErrorCount != null) {
                            cardsAndErrors[Pair(entry.key,entry.value)] = newErrorCount.toInt()
                        }
                    }
                    break
                }
                index++
            }
        }
    }

    fun hardestCardFunc(){
        var highest = 0
        for (entry in cardsAndErrors) {
            if (entry.value > highest) {
                highest = entry.value
            }
        }
        if (highest == 0) {
            logger.logOutput("There are no cards with errors.")
            return
        }
        else{
            val hardestCards = mutableListOf<String>()
            for (entry in cardsAndErrors) {
                if (entry.value == highest) {
                    hardestCards.add("\"${entry.key.first}\"")
                }
            }
            if (hardestCards.size > 1){
                logger.logOutput("The hardest cards are ${hardestCards.joinToString(separator = ", ")}. " +
                        "You have $highest errors answering them.")
            }
            else{
                logger.logOutput("The hardest card is ${hardestCards.joinToString()}. You have $highest errors answering it.")
            }
        }
    }

    fun resetStats(){
        for (entry in cardsAndErrors){
            entry.setValue(0)
        }
        logger.logOutput("Card statistics have been reset.")
    }

}