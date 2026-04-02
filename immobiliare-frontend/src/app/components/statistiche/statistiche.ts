import { Component, OnInit, AfterViewInit, ElementRef, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnnuncioService } from '../../services/annuncio';
import { CategoriaService } from '../../services/categoria';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-statistiche',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './statistiche.html',
  styleUrl: './statistiche.css'
})
export class Statistiche implements OnInit {

  @ViewChild('graficoCategorie') graficoCategorie!: ElementRef;
  @ViewChild('graficoTipi') graficoTipi!: ElementRef;
  @ViewChild('graficoPrezzi') graficoPrezzi!: ElementRef;

  annunci: any[] = [];
  categorie: any[] = [];
  totaleAnnunci = 0;
  prezzoMedio = 0;
  inAsta = 0;

  animatedTotale = 0;
  animatedPrezzo = 0;
  animatedAsta = 0;
  barTotale = 0;
  barPrezzo = 0;
  barAsta = 0;

  datiPronti = false;

  constructor(
    private annuncioService: AnnuncioService,
    private categoriaService: CategoriaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.categoriaService.getAll().subscribe({
      next: (cat: any) => {
        this.categorie = cat;
        this.annuncioService.getAll().subscribe({
          next: (data: any) => {
            this.annunci = data;
            this.totaleAnnunci = data.length;
            this.prezzoMedio = data.length > 0
              ? Math.round(data.reduce((acc: number, a: any) => acc + a.prezzo, 0) / data.length)
              : 0;
            this.inAsta = data.filter((a: any) => a.inAsta).length;
            this.datiPronti = true;
            this.cdr.detectChanges();
            setTimeout(() => {
              this.animaNumeri();
              this.creaGrafici();
            }, 50);
          },
          error: (err: any) => console.error(err)
        });
      },
      error: (err: any) => console.error(err)
    });
  }

  animaNumeri() {
    const durata = 1500;
    const passi = 60;
    const intervallo = durata / passi;
    let step = 0;

    const timer = setInterval(() => {
      step++;
      const progress = step / passi;
      const eased = 1 - Math.pow(1 - progress, 3);

      this.animatedTotale = Math.round(this.totaleAnnunci * eased);
      this.animatedPrezzo = Math.round(this.prezzoMedio * eased);
      this.animatedAsta = Math.round(this.inAsta * eased);
      this.barTotale = Math.min(100, eased * 100);
      this.barPrezzo = Math.min(100, eased * 85);
      this.barAsta = this.totaleAnnunci > 0
        ? Math.min(100, (this.inAsta / this.totaleAnnunci) * 100 * eased * 3)
        : 0;

      this.cdr.detectChanges();

      if (step >= passi) clearInterval(timer);
    }, intervallo);
  }

  creaGrafici() {
    setTimeout(() => {
      this.graficoPerCategoria();
      this.graficoPerTipo();
      this.graficoPerPrezzo();
    }, 100);
  }

  graficoPerCategoria() {
    const conteggi: { [key: string]: number } = {};
    this.annunci.forEach(a => {
      const cat = this.categorie.find((c: any) => c.id === a.idCategoria);
      const nome = cat ? cat.nome : 'Altro';
      conteggi[nome] = (conteggi[nome] || 0) + 1;
    });

    new Chart(this.graficoCategorie.nativeElement, {
      type: 'doughnut',
      data: {
        labels: Object.keys(conteggi),
        datasets: [{
          data: Object.values(conteggi),
          backgroundColor: ['#c0001a', '#1a1a1a', '#888', '#e8e8e8', '#f0a0a0', '#666'],
          borderWidth: 3,
          borderColor: '#fff'
        }]
      },
      options: {
        responsive: true,
        animation: { duration: 1000, easing: 'easeInOutQuart' },
        plugins: {
          legend: { position: 'bottom', labels: { font: { size: 12 } } }
        }
      }
    });
  }

  graficoPerTipo() {
    const vendita = this.annunci.filter(a => a.tipoOperazione === 'VENDITA').length;
    const affitto = this.annunci.filter(a => a.tipoOperazione === 'AFFITTO').length;

    new Chart(this.graficoTipi.nativeElement, {
      type: 'bar',
      data: {
        labels: ['Vendita', 'Affitto'],
        datasets: [{
          label: 'Numero annunci',
          data: [vendita, affitto],
          backgroundColor: ['#c0001a', '#1a1a1a'],
          borderRadius: 6
        }]
      },
      options: {
        responsive: true,
        animation: { duration: 1000, easing: 'easeInOutQuart' },
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } }
      }
    });
  }

  graficoPerPrezzo() {
    const fasce: { [key: string]: number } = {
      '< 50k': 0,
      '50k - 150k': 0,
      '150k - 300k': 0,
      '300k - 500k': 0,
      '> 500k': 0
    };

    this.annunci.forEach(a => {
      if (a.prezzo < 50000) fasce['< 50k']++;
      else if (a.prezzo < 150000) fasce['50k - 150k']++;
      else if (a.prezzo < 300000) fasce['150k - 300k']++;
      else if (a.prezzo < 500000) fasce['300k - 500k']++;
      else fasce['> 500k']++;
    });

    new Chart(this.graficoPrezzi.nativeElement, {
      type: 'bar',
      data: {
        labels: Object.keys(fasce),
        datasets: [{
          label: 'Annunci',
          data: Object.values(fasce),
          backgroundColor: ['#c0001a', '#d94030', '#e86050', '#888', '#1a1a1a'],
          borderRadius: 6
        }]
      },
      options: {
        responsive: true,
        animation: { duration: 1000, easing: 'easeInOutQuart' },
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } }
      }
    });
  }
}