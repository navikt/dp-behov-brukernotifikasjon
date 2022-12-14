package no.nav.dagpenger.behov.brukernotifikasjon.tjenester

import io.mockk.mockk
import io.mockk.verify
import no.nav.dagpenger.behov.brukernotifikasjon.notifikasjoner.Beskjed
import no.nav.dagpenger.behov.brukernotifikasjon.notifikasjoner.BeskjedTopic
import no.nav.dagpenger.behov.brukernotifikasjon.notifikasjoner.DoneTopic
import no.nav.dagpenger.behov.brukernotifikasjon.notifikasjoner.OppgaveTopic
import org.junit.jupiter.api.Test

internal class NotifikasjonerTest {
    private val beskjedTopic: BeskjedTopic = mockk(relaxed = true)
    private val oppgaveTopic: OppgaveTopic = mockk(relaxed = true)
    private val doneTopic: DoneTopic = mockk(relaxed = true)
    private val notifikasjoner = Notifikasjoner(
        repository = mockk(relaxed = true),
        beskjedTopic,
        oppgaveTopic,
        doneTopic
    )

    @Test
    fun test() {
        notifikasjoner.send(
            Beskjed(
                Ident("12312312311"),
                "1-2-3 nå kommer en beskjed"
            )
        )

        verify {
            beskjedTopic.publiser(any(), any())
        }
    }
}
