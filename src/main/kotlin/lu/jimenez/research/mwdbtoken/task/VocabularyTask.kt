package lu.jimenez.research.mwdbtoken.task

import lu.jimenez.research.mwdbtoken.Constants.*
import lu.jimenez.research.mwdbtoken.actions.MwdbTokenActions.retrieveVocabularyNode
import lu.jimenez.research.mylittleplugin.MyLittleActions.*
import mu.KLogging
import org.mwg.Constants.*
import org.mwg.Type
import org.mwg.task.Task
import org.mwg.task.Tasks.newTask

object VocabularyTask : KLogging() {

    /**
     * Create a NodeIndex vocabulary
     */
    @JvmStatic
    fun initializeVocabulary(): Task {
        return newTask()
                .then(executeAtWorldAndTime("0", "$BEGINNING_OF_TIME",
                        newTask()
                                .createNode()
                                .setAttribute(ENTRY_POINT_NODE_NAME, Type.STRING, VOCABULARY_NODE_NAME)
                                .timeSensitivity("$END_OF_TIME", "0")
                                .addToGlobalIndex(ENTRY_POINT_INDEX, ENTRY_POINT_NODE_NAME)
                ))

    }

    @JvmStatic
    fun retrieveVocabulary(): Task {
        return newTask()
                .readGlobalIndex(ENTRY_POINT_INDEX, ENTRY_POINT_NODE_NAME, VOCABULARY_NODE_NAME)
    }

    @JvmStatic
    fun getOrCreateTokensFromString(tokens: Array<String>): Task {
        return newTask()
                .then(retrieveVocabularyNode())
                .defineAsVar("Vocabulary")
                .inject(tokens)
                .map(retrieveToken())
                .flat()
    }

    fun retrieveToken(): Task {
        return newTask()
                .defineAsVar("token")
                .readVar("Vocabulary")
                .traverse(VOCABULARY_TOKEN_INDEX, TOKEN_NAME, "{{token}}")
                .then(
                        ifEmptyThen(
                                createToken()
                        )
                )
    }

    private fun createToken(): Task {
        return newTask()
                .then(executeAtWorldAndTime(
                        "0",
                        "$BEGINNING_OF_TIME",
                        newTask()
                                //Token
                                .createNode()
                                .timeSensitivity("$END_OF_TIME", "0")
                                .setAttribute(TOKEN_NAME, Type.STRING, "{{token}}")
                                .defineAsVar("newToken")
                                .readVar("Vocabulary")
                                .addVarToRelation(VOCABULARY_TOKEN_INDEX, "newToken", TOKEN_NAME)
                                .readVar("newToken")
                                //invertedIndex
                                /**.createNode()
                                .timeSensitivity(Long.MAX_VALUE, 0)
                                .setAttribute("name", Type.STRING, "invertedIndex")
                                .defineAsVar("invertedIndex")
                                .addVarToRelation(INVERTED_INDEX_WORD_RELATION, "newToken")
                                .readVar("newToken")
                                .addVarToRelation(WORD_INVERTED_INDEX_RELATION, "invertedIndex")*/
                ))

        //}

    }

}