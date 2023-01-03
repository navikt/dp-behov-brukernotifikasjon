package no.nav.dagpenger.behov.brukernotifikasjon.tjenester

import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.nav.dagpenger.behov.brukernotifikasjon.notifikasjoner.Oppgave
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertContains

internal class EttersendingsoppgaveRiverTest {
    private val ettersendelseHandler = mockk<EttersendelseHandler>(relaxed = true)
    private val rapid by lazy {
        TestRapid().apply {
            EttersendingsoppgaveRiver(this, ettersendelseHandler)
        }
    }

    init {
        System.setProperty("brukernotifikasjon.oppgave.topic", "data")
    }

    @AfterEach
    fun cleanUp() {
        rapid.reset()
    }

    @Test
    fun `skal publisere brukernotifikasjoner`() {
        rapid.sendTestMessage(ettersendelseoppgaveBehov.toJson())

        val opprettetOppgave = slot<Oppgave>()

        verify {
            ettersendelseHandler.opprettHvisIkkeFinnesFraFør(capture(opprettetOppgave))
        }

        assertContains(opprettetOppgave.captured.getSnapshot().link.toString(), søknadId.toString())
    }
}

private val søknadId = UUID.randomUUID()

val ettersendelseoppgaveBehov = JsonMessage.newNeed(
    behov = listOf("brukernotifikasjon"),
    map = mapOf(
        "type" to "ettersendingsoppgave",
        "ident" to "12312312312",
        "tekst" to "1-2-3 nå kommer en oppgave",
        "link" to "https://url.til.oppgaven/123",
        "søknad_uuid" to søknadId
    )
)