package no.nav.dagpenger.behov.brukernotifikasjon.tjenester

import com.fasterxml.jackson.databind.JsonNode
import mu.KotlinLogging
import mu.withLoggingContext
import no.nav.dagpenger.behov.brukernotifikasjon.notifikasjoner.Beskjed
import no.nav.helse.rapids_rivers.*
import java.util.*

internal class BeskjedRiver(
    rapidsConnection: RapidsConnection,
    private val notifikasjoner: Notifikasjoner
) : River.PacketListener {
    init {
        River(rapidsConnection).apply {
            validate { it.demandValue("@event_name", "behov") }
            validate { it.demandAllOrAny("@behov", listOf("brukernotifikasjon")) }
            validate { it.requireValue("type", "beskjed") }
            validate {
                it.requireKey(
                    "@behovId",
                    "@opprettet",
                    "ident",
                    "tekst"
                )
            }

            validate {
                it.interestedIn(
                    "link"
                )
            }
        }.register(this)
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val behovId = packet["@behovId"].asUUID()
        val ident = packet["ident"].asText()

        withLoggingContext(
            "behovId" to behovId.toString()
        ) {
            logger.info { "Løser behov for brukernotifikasjon: beskjed" }

            notifikasjoner.send(
                Beskjed(
                    eventId = behovId,
                    ident = Ident(ident),
                    tekst = packet["tekst"].asText(),
                    opprettet = packet["@opprettet"].asLocalDateTime(),
                )
            )
        }
    }
}

internal fun JsonNode.asUUID(): UUID = this.asText().let { UUID.fromString(it) }
