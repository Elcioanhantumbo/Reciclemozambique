
class AgendaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Layout simples ou direto para ScheduleCollection
        val intent = Intent(this, ScheduleCollectionActivity::class.java)
        startActivity(intent)
        finish()  // Ou use como container
    }
}