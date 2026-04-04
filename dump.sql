--
-- PostgreSQL database dump
--

\restrict S3JWmTikT0zGbhu67ltoLXLNNvVRO1WvcKbj7iNBIC5kQUqxvbo4t8Ip0KY7ZE3

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

-- Started on 2026-04-04 12:54:43

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 224 (class 1259 OID 24665)
-- Name: annuncio; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.annuncio (
    id integer NOT NULL,
    titolo character varying(200) NOT NULL,
    descrizione text,
    prezzo numeric(12,2) NOT NULL,
    prezzo_ribassato numeric(12,2),
    metri_quadri integer,
    tipo_operazione character varying(10) NOT NULL,
    indirizzo character varying(300),
    latitudine double precision,
    longitudine double precision,
    in_asta boolean DEFAULT false NOT NULL,
    id_venditore integer NOT NULL,
    id_categoria integer NOT NULL,
    data_inserimento timestamp without time zone DEFAULT now() NOT NULL,
    stato character varying(20) DEFAULT 'IN_ATTESA'::character varying,
    num_bagni integer,
    num_locali integer,
    numero_modifiche integer DEFAULT 0,
    CONSTRAINT annuncio_tipo_operazione_check CHECK (((tipo_operazione)::text = ANY ((ARRAY['VENDITA'::character varying, 'AFFITTO'::character varying])::text[])))
);


ALTER TABLE public.annuncio OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 24664)
-- Name: annuncio_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.annuncio_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.annuncio_id_seq OWNER TO postgres;

--
-- TOC entry 5160 (class 0 OID 0)
-- Dependencies: 223
-- Name: annuncio_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.annuncio_id_seq OWNED BY public.annuncio.id;


--
-- TOC entry 232 (class 1259 OID 24764)
-- Name: asta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.asta (
    id integer NOT NULL,
    id_annuncio integer NOT NULL,
    prezzo_base numeric(12,2) NOT NULL,
    offerta_max numeric(12,2),
    id_offerente integer,
    data_scadenza timestamp without time zone NOT NULL,
    attiva boolean DEFAULT true NOT NULL
);


ALTER TABLE public.asta OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 24763)
-- Name: asta_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.asta_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.asta_id_seq OWNER TO postgres;

--
-- TOC entry 5161 (class 0 OID 0)
-- Dependencies: 231
-- Name: asta_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.asta_id_seq OWNED BY public.asta.id;


--
-- TOC entry 220 (class 1259 OID 24634)
-- Name: categoria; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categoria (
    id integer NOT NULL,
    nome character varying(100) NOT NULL,
    ordine integer DEFAULT 0
);


ALTER TABLE public.categoria OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 24633)
-- Name: categoria_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.categoria_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.categoria_id_seq OWNER TO postgres;

--
-- TOC entry 5162 (class 0 OID 0)
-- Dependencies: 219
-- Name: categoria_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.categoria_id_seq OWNED BY public.categoria.id;


--
-- TOC entry 226 (class 1259 OID 24695)
-- Name: foto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.foto (
    id integer NOT NULL,
    id_annuncio integer NOT NULL,
    url character varying(500) NOT NULL
);


ALTER TABLE public.foto OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 24694)
-- Name: foto_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.foto_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.foto_id_seq OWNER TO postgres;

--
-- TOC entry 5163 (class 0 OID 0)
-- Dependencies: 225
-- Name: foto_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.foto_id_seq OWNED BY public.foto.id;


--
-- TOC entry 230 (class 1259 OID 24738)
-- Name: messaggio; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.messaggio (
    id integer NOT NULL,
    id_annuncio integer,
    id_mittente integer NOT NULL,
    oggetto character varying(300) NOT NULL,
    testo text NOT NULL,
    data_invio timestamp without time zone DEFAULT now() NOT NULL,
    letto boolean DEFAULT false,
    eliminato_venditore boolean DEFAULT false,
    eliminato_acquirente boolean DEFAULT false,
    per_admin boolean DEFAULT false
);


ALTER TABLE public.messaggio OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 24737)
-- Name: messaggio_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.messaggio_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.messaggio_id_seq OWNER TO postgres;

--
-- TOC entry 5164 (class 0 OID 0)
-- Dependencies: 229
-- Name: messaggio_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.messaggio_id_seq OWNED BY public.messaggio.id;


--
-- TOC entry 234 (class 1259 OID 24787)
-- Name: offerta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.offerta (
    id integer NOT NULL,
    id_asta integer NOT NULL,
    id_utente integer NOT NULL,
    importo numeric(12,2) NOT NULL,
    data_offerta timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.offerta OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 24786)
-- Name: offerta_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.offerta_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.offerta_id_seq OWNER TO postgres;

--
-- TOC entry 5165 (class 0 OID 0)
-- Dependencies: 233
-- Name: offerta_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.offerta_id_seq OWNED BY public.offerta.id;


--
-- TOC entry 236 (class 1259 OID 24810)
-- Name: preferito; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.preferito (
    id integer NOT NULL,
    id_utente integer NOT NULL,
    id_annuncio integer NOT NULL
);


ALTER TABLE public.preferito OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 24809)
-- Name: preferito_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.preferito_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.preferito_id_seq OWNER TO postgres;

--
-- TOC entry 5166 (class 0 OID 0)
-- Dependencies: 235
-- Name: preferito_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.preferito_id_seq OWNED BY public.preferito.id;


--
-- TOC entry 228 (class 1259 OID 24712)
-- Name: recensione; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.recensione (
    id integer NOT NULL,
    id_annuncio integer NOT NULL,
    id_utente integer NOT NULL,
    punteggio integer NOT NULL,
    commento text,
    data_rec timestamp without time zone DEFAULT now() NOT NULL,
    CONSTRAINT recensione_punteggio_check CHECK (((punteggio >= 1) AND (punteggio <= 5)))
);


ALTER TABLE public.recensione OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 24711)
-- Name: recensione_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.recensione_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.recensione_id_seq OWNER TO postgres;

--
-- TOC entry 5167 (class 0 OID 0)
-- Dependencies: 227
-- Name: recensione_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.recensione_id_seq OWNED BY public.recensione.id;


--
-- TOC entry 238 (class 1259 OID 24844)
-- Name: richiesta_promozione; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.richiesta_promozione (
    id integer NOT NULL,
    id_utente integer,
    stato character varying(20) DEFAULT 'IN_ATTESA'::character varying,
    data_richiesta timestamp without time zone DEFAULT now()
);


ALTER TABLE public.richiesta_promozione OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 24843)
-- Name: richiesta_promozione_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.richiesta_promozione_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.richiesta_promozione_id_seq OWNER TO postgres;

--
-- TOC entry 5168 (class 0 OID 0)
-- Dependencies: 237
-- Name: richiesta_promozione_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.richiesta_promozione_id_seq OWNED BY public.richiesta_promozione.id;


--
-- TOC entry 240 (class 1259 OID 33037)
-- Name: segnalazione; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.segnalazione (
    id integer NOT NULL,
    id_annuncio bigint,
    id_segnalante bigint,
    motivo text NOT NULL,
    categoria character varying(50) NOT NULL,
    stato character varying(20) DEFAULT 'IN_ATTESA'::character varying,
    data_inserimento timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.segnalazione OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 33036)
-- Name: segnalazione_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.segnalazione_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.segnalazione_id_seq OWNER TO postgres;

--
-- TOC entry 5169 (class 0 OID 0)
-- Dependencies: 239
-- Name: segnalazione_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.segnalazione_id_seq OWNED BY public.segnalazione.id;


--
-- TOC entry 222 (class 1259 OID 24645)
-- Name: utente; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.utente (
    id integer NOT NULL,
    nome character varying(100) NOT NULL,
    cognome character varying(100) NOT NULL,
    email character varying(150) NOT NULL,
    password character varying(255) NOT NULL,
    ruolo character varying(20) NOT NULL,
    bannato boolean DEFAULT false NOT NULL,
    foto_profilo character varying(500),
    email_verificata boolean DEFAULT false,
    token_verifica character varying(255),
    token_reset character varying(255),
    token_reset_scadenza timestamp without time zone,
    CONSTRAINT utente_ruolo_check CHECK (((ruolo)::text = ANY ((ARRAY['AMMINISTRATORE'::character varying, 'VENDITORE'::character varying, 'ACQUIRENTE'::character varying])::text[])))
);


ALTER TABLE public.utente OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 24644)
-- Name: utente_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.utente_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.utente_id_seq OWNER TO postgres;

--
-- TOC entry 5170 (class 0 OID 0)
-- Dependencies: 221
-- Name: utente_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.utente_id_seq OWNED BY public.utente.id;


--
-- TOC entry 4911 (class 2604 OID 24668)
-- Name: annuncio id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.annuncio ALTER COLUMN id SET DEFAULT nextval('public.annuncio_id_seq'::regclass);


--
-- TOC entry 4925 (class 2604 OID 24767)
-- Name: asta id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.asta ALTER COLUMN id SET DEFAULT nextval('public.asta_id_seq'::regclass);


--
-- TOC entry 4906 (class 2604 OID 24637)
-- Name: categoria id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categoria ALTER COLUMN id SET DEFAULT nextval('public.categoria_id_seq'::regclass);


--
-- TOC entry 4916 (class 2604 OID 24698)
-- Name: foto id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.foto ALTER COLUMN id SET DEFAULT nextval('public.foto_id_seq'::regclass);


--
-- TOC entry 4919 (class 2604 OID 24741)
-- Name: messaggio id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messaggio ALTER COLUMN id SET DEFAULT nextval('public.messaggio_id_seq'::regclass);


--
-- TOC entry 4927 (class 2604 OID 24790)
-- Name: offerta id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offerta ALTER COLUMN id SET DEFAULT nextval('public.offerta_id_seq'::regclass);


--
-- TOC entry 4929 (class 2604 OID 24813)
-- Name: preferito id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preferito ALTER COLUMN id SET DEFAULT nextval('public.preferito_id_seq'::regclass);


--
-- TOC entry 4917 (class 2604 OID 24715)
-- Name: recensione id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.recensione ALTER COLUMN id SET DEFAULT nextval('public.recensione_id_seq'::regclass);


--
-- TOC entry 4930 (class 2604 OID 24847)
-- Name: richiesta_promozione id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.richiesta_promozione ALTER COLUMN id SET DEFAULT nextval('public.richiesta_promozione_id_seq'::regclass);


--
-- TOC entry 4933 (class 2604 OID 33040)
-- Name: segnalazione id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.segnalazione ALTER COLUMN id SET DEFAULT nextval('public.segnalazione_id_seq'::regclass);


--
-- TOC entry 4908 (class 2604 OID 24648)
-- Name: utente id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utente ALTER COLUMN id SET DEFAULT nextval('public.utente_id_seq'::regclass);


--
-- TOC entry 5138 (class 0 OID 24665)
-- Dependencies: 224
-- Data for Name: annuncio; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.annuncio (id, titolo, descrizione, prezzo, prezzo_ribassato, metri_quadri, tipo_operazione, indirizzo, latitudine, longitudine, in_asta, id_venditore, id_categoria, data_inserimento, stato, num_bagni, num_locali, numero_modifiche) FROM stdin;
87	Chalet in montagna	Splendida villa di 200 mq immersa nel verde e nella quiete di Trento, ideale per chi cerca il perfetto connubio tra natura e comfort, offrendo ben 8 locali luminosi e 2 bagni per accogliere la vostra famiglia e i vostri ospiti. Questa proprietà rappresenta un'opportunità unica per vivere la montagna in ogni stagione, godendo di ampi spazi e di una posizione invidiabile. Perfetta sia come residenza principale che come rifugio d'eccezione, è pronta ad accogliere i vostri sogni.	500000.00	\N	200	VENDITA	Trento, Via Vallelaghi	46.113053	11.071277	f	3	2	2026-04-04 12:20:19.226934	APPROVATO	\N	\N	1
11	Appartamento per studenti	Appartamento in zona centrale,vicino alla stazione e alla fermata dei pullman per l'Università	300.00	\N	75	AFFITTO	Via Marconi 2	39.3312	16.1805	f	2	1	2026-03-27 23:10:09.509248	RIFIUTATO	\N	\N	0
26	terreno	terreno edificabile	30000.00	\N	300000	VENDITA	Via Alberto Savino	39.354762	16.228587	f	2	4	2026-03-28 00:39:50.574092	RIFIUTATO	\N	\N	0
14	villa 		200000.00	\N	200000	VENDITA	Corso Mazzini	39.298065	16.253706	f	2	2	2026-03-27 23:47:54.187339	RIFIUTATO	\N	\N	0
10	Villa con piscina e giardino	Confortevole villa,spaziosa e dotata di tutti i confort. 	600000.00	\N	350	VENDITA	Cosenza, Via Domenico Morelli	39.2936	16.2259	f	2	2	2026-03-24 10:41:09.656099	APPROVATO	\N	\N	0
15	terreno 		30000.00	\N	3000	VENDITA	Via Alberto Savino	39.354762	16.228587	f	2	4	2026-03-28 00:22:18.192174	APPROVATO	\N	\N	0
16	terreno		30000.00	\N	300000	VENDITA		39.354762	16.228587	f	2	4	2026-03-28 00:24:06.49522	RIFIUTATO	\N	\N	0
17	terreno		30000.00	\N	300000	VENDITA		39.354762	16.228587	f	2	4	2026-03-28 00:36:53.41989	RIFIUTATO	\N	\N	0
18	terreno		30000.00	\N	300000	VENDITA		39.354762	16.228587	f	2	4	2026-03-28 00:36:57.055104	RIFIUTATO	\N	\N	0
19	terreno		30000.00	\N	300000	VENDITA	via Alberto Savino	39.354762	16.228587	f	2	4	2026-03-28 00:37:14.203154	RIFIUTATO	\N	\N	0
20	terreno		30000.00	\N	300000	VENDITA	via Alberto Savino	39.354762	16.228587	f	2	4	2026-03-28 00:37:25.75379	RIFIUTATO	\N	\N	0
21	terreno		30000.00	\N	300000	VENDITA	via Alberto Savino	39.354762	16.228587	f	2	4	2026-03-28 00:37:37.83871	RIFIUTATO	\N	\N	0
22	terreno		30000.00	\N	300000	VENDITA	Via Alberto Savino	39.354762	16.228587	f	2	4	2026-03-28 00:38:18.9391	RIFIUTATO	\N	\N	0
23	terreno		30000.00	\N	300000	VENDITA	Via Alberto Savino	39.354762	16.228587	f	2	4	2026-03-28 00:38:24.207452	RIFIUTATO	\N	\N	0
24	terreno	terreno edificabile	30000.00	\N	300000	VENDITA	Via Alberto Savino	39.354762	16.228587	f	2	4	2026-03-28 00:39:31.60398	RIFIUTATO	\N	\N	0
25	terreno	terreno edificabile	30000.00	\N	300000	VENDITA	Via Alberto Savino	39.354762	16.228587	f	2	4	2026-03-28 00:39:37.340676	RIFIUTATO	\N	\N	0
27	terreno	terreno edificabile	30000.00	\N	300000	VENDITA	Via Alberto Savino	39.354762	16.228587	f	2	4	2026-03-28 00:40:36.175949	RIFIUTATO	\N	\N	0
7	Box auto centro	Box auto comodo, vicino al centro	25000.00	\N	15	VENDITA	Cosenza, Via Roma 8	39.299	16.255	f	2	3	2026-03-21 21:50:19.569501	APPROVATO	\N	\N	0
3	Box auto centro	Box auto comodo, vicino al centro	25000.00	\N	15	VENDITA	Cosenza, Via Roma 8	39.299	16.255	f	2	3	2026-03-21 21:49:19.992217	APPROVATO	\N	\N	0
28	t		30000.00	\N	300000	VENDITA	via alberto savino	39.354762	16.228587	f	2	4	2026-03-28 00:58:44.489813	APPROVATO	\N	\N	0
8	Appartamento in affitto	Monolocale arredato, zona universitaria	400.00	\N	40	AFFITTO	Cosenza, Via Mazzini 3	39.297	16.253	f	2	1	2026-03-21 21:50:19.569501	APPROVATO	\N	\N	0
4	Appartamento in affitto	Monolocale arredato, zona universitaria	400.00	\N	40	AFFITTO	Cosenza, Via Mazzini 3	39.297	16.253	f	2	1	2026-03-21 21:49:19.992217	APPROVATO	\N	\N	0
32	casa vancanze		70000.00	\N	100	VENDITA	Tropea, Via Roma	765666	76767676	f	2	1	2026-03-28 15:05:47.056528	APPROVATO	\N	\N	2
1	Appartamento luminoso centro	Bellissimo appartamento al terzo piano con vista panoramica.	250000.00	\N	85	VENDITA	Cosenza, Via Roma 10	39.2984	16.2542	f	2	1	2026-03-21 19:19:24.284309	APPROVATO	\N	\N	0
6	Villa con piscina	Splendida villa con piscina e giardino	850000.00	\N	300	VENDITA	Cosenza, Via Nazionale 45	39.3012	16.258	f	2	2	2026-03-21 21:50:19.569501	APPROVATO	\N	\N	0
2	Villa con piscina	Splendida villa con piscina e giardino	850000.00	\N	300	VENDITA	Cosenza, Via Nazionale 45	39.3012	16.258	f	2	2	2026-03-21 21:49:19.992217	APPROVATO	\N	\N	0
9	Ufficio commerciale	Ufficio al secondo piano con parcheggio	1200.00	\N	80	AFFITTO	Cosenza, Corso Mazzini 12	39.2995	16.256	f	2	5	2026-03-21 21:50:19.569501	APPROVATO	\N	\N	0
88	Casa in affitto	Splendido appartamento di 50 mq completamente ristrutturato, ideale per single o coppie, situato nel cuore di Genova, offre 3 locali luminosi e accoglienti, con un bagno moderno e funzionale. Questa soluzione abitativa rappresenta un'opportunità imperdibile per vivere in una delle città più affascinanti d'Italia, combinando comfort, stile e una posizione invidiabile. Non lasciatevi sfuggire questa chicca genovese, perfetta per chi cerca un nido elegante e pratico.	400.00	\N	50	AFFITTO	Genova, Sestri Ponente	44.432005	8.872861	f	2	1	2026-04-04 12:36:44.142171	APPROVATO	\N	\N	1
51	Box auto coperto garage	Box auto singolo in condominio sicuro con cancello automatico, vicino al centro città.	18000.00	\N	15	VENDITA	Cosenza, Via Trieste 7	39.3021	16.2587	f	15	5	2026-04-04 00:13:06.007623	APPROVATO	0	1	0
5	Ufficio commerciale	Ufficio al secondo piano con parcheggio	1200.00	\N	80	AFFITTO	Cosenza, Corso Mazzini 12	39.2995	16.256	f	2	5	2026-03-21 21:49:19.992217	APPROVATO	\N	\N	0
12	Appaartamento per studenti	Appartamento centrale 	300.00	\N	50	AFFITTO	Rende, Via Marconi 23	39.352756	16.239155	f	2	1	2026-03-27 23:12:28.442523	APPROVATO	\N	\N	0
30	Appartamento in riva al mare	Adatto ai periodi estivi	50000.00	\N	45	VENDITA	Scalea, Corso Mediterraneo 5	39.80459	15.791	t	2	1	2026-03-28 11:26:45.181499	APPROVATO	\N	\N	1
47	Appartamento luminoso centro storico	Bellissimo appartamento al terzo piano con vista panoramica, recentemente ristrutturato con finiture di pregio.	185000.00	\N	85	VENDITA	Cosenza, Via Roma 12	39.2984	16.2542	f	15	1	2026-04-04 00:13:06.007623	APPROVATO	1	3	0
48	Villa con piscina collina	Splendida villa su due livelli con ampio giardino, piscina e garage doppio. Posizione tranquilla e panoramica.	520000.00	\N	280	VENDITA	Rende, Via Panoramica 5	39.3317	16.1814	f	15	2	2026-04-04 00:13:06.007623	APPROVATO	3	7	0
49	Ufficio moderno zona business	Ufficio open space al secondo piano con reception, sala riunioni e parcheggio riservato.	1200.00	\N	120	AFFITTO	Cosenza, Viale della Repubblica 33	39.3012	16.2498	f	15	3	2026-04-04 00:13:06.007623	APPROVATO	2	4	0
50	Locale commerciale centro	Locale su strada principale con vetrina, magazzino e bagno. Ottima visibilità e passaggio pedonale elevato.	900.00	\N	65	AFFITTO	Cosenza, Corso Mazzini 88	39.2991	16.2531	f	15	4	2026-04-04 00:13:06.007623	APPROVATO	1	2	0
52	Terreno edificabile zona espansione	Terreno pianeggiante con progetto approvato per costruzione residenziale, allacciamenti disponibili.	95000.00	\N	500	VENDITA	Rende, Via delle Magnolie	39.3289	16.1756	f	15	6	2026-04-04 00:13:06.007623	APPROVATO	0	0	0
53	Appartamento bilocale zona universitaria	Bilocale ideale per studenti o giovani coppie, arredato, vicino all università e mezzi pubblici.	650.00	\N	45	AFFITTO	Rende, Via Unical 3	39.3601	16.2253	f	15	1	2026-04-04 00:13:06.007623	APPROVATO	1	2	0
54	Villa indipendente con giardino	Villa singola su tre livelli, ampio giardino privato, cantina, garage e terrazza panoramica.	380000.00	\N	220	VENDITA	Castrolibero, Via dei Pini 14	39.3198	16.2043	f	15	2	2026-04-04 00:13:06.007623	APPROVATO	2	6	0
55	Appartamento trilocale ristrutturato	Trilocale al quinto piano con ascensore, completamente ristrutturato, doppi servizi e balcone.	145000.00	\N	95	VENDITA	Cosenza, Via Piave 21	39.3045	16.2612	f	15	1	2026-04-04 00:13:06.007623	APPROVATO	2	3	0
56	Ufficio direzionale piano alto	Ufficio con vista città, sala conferenze, angolo cottura e due bagni. Adatto a studi professionali.	1800.00	\N	180	AFFITTO	Cosenza, Via Alimena 10	39.2978	16.2501	f	15	3	2026-04-04 00:13:06.007623	APPROVATO	2	5	0
57	Locale commerciale periferia	Ampio locale con ingresso indipendente, magazzino, bagno e parcheggio esterno. Ideale per attività commerciale.	750.00	\N	90	AFFITTO	Rende, Via Nazionale 55	39.3342	16.1823	f	15	4	2026-04-04 00:13:06.007623	APPROVATO	1	2	0
58	Appartamento attico con terrazza	Attico esclusivo con grande terrazza abitabile, vista mozzafiato sulla città, finiture di lusso.	290000.00	\N	130	VENDITA	Cosenza, Via Caloprese 8	39.3067	16.2578	f	15	1	2026-04-04 00:13:06.007623	APPROVATO	\N	\N	7
59	Box auto doppio posto	Doppio box auto con ingresso automatizzato, impianto elettrico e presa acqua, zona residenziale tranquilla.	28000.00	\N	28	VENDITA	Cosenza, Via Labonia 3	39.3089	16.2634	f	15	5	2026-04-04 00:13:06.007623	APPROVATO	0	1	0
60	Terreno agricolo con ulivi	Terreno agricolo con uliveto secolare, pozzo artesiano e piccolo fabbricato rurale da ristrutturare.	45000.00	\N	3000	VENDITA	Bisignano, Contrada Muoio	39.5134	16.2891	f	15	6	2026-04-04 00:13:06.007623	APPROVATO	0	0	0
61	Villa bifamiliare moderna	Villa bifamiliare di nuova costruzione, classe energetica A, pannelli solari, giardino e posto auto.	430000.00	\N	195	VENDITA	Montalto Uffugo, Via delle Rose 22	39.4012	16.1534	f	15	2	2026-04-04 00:13:06.007623	APPROVATO	\N	\N	1
62	Appartamento monolocale affitto breve	Monolocale arredato con gusto, zona centrale, wifi incluso, ideale per trasferte o brevi soggiorni.	500.00	\N	32	AFFITTO	Cosenza, Via Montesanto 6	39.2967	16.2489	f	15	1	2026-04-04 00:13:06.007623	APPROVATO	1	1	0
63	Ufficio condiviso coworking	Spazio coworking con postazioni fisse e variabili, sala meeting, stampante e connessione fibra inclusa.	400.00	\N	60	AFFITTO	Cosenza, Via degli Stadi 18	39.3078	16.2601	f	15	3	2026-04-04 00:13:06.007623	APPROVATO	1	3	0
64	Locale commerciale angolo strada	Locale d angolo con doppia vetrina, grande visibilità, recentemente ristrutturato con impianti a norma.	1100.00	\N	75	AFFITTO	Cosenza, Via Popilia 44	39.3156	16.2712	f	15	4	2026-04-04 00:13:06.007623	APPROVATO	1	2	0
65	Appartamento quadrilocale signorile	Ampio quadrilocale in palazzo signorile con portiere, doppi vetri, parquet e cantina inclusa.	210000.00	\N	140	VENDITA	Cosenza, Viale Parco 9	39.3034	16.2556	f	15	1	2026-04-04 00:13:06.007623	APPROVATO	2	4	0
67	Appartamento vista Colosseo	Splendido appartamento ristrutturato a pochi passi dal Colosseo, finiture di lusso e terrazza panoramica.	450000.00	\N	110	VENDITA	Roma, Via Labicana 28	41.8902	12.4922	f	2	1	2026-04-04 00:14:53.232094	APPROVATO	2	3	0
68	Villa con piscina Toscana	Magnifica villa immersa nelle colline toscane con piscina, uliveto e dependance per ospiti.	890000.00	\N	350	VENDITA	Greve in Chianti, Via del Chianti 15	43.5847	11.3134	f	2	2	2026-04-04 00:14:53.232094	APPROVATO	4	8	0
69	Ufficio moderno Milano Porta Nuova	Ufficio di rappresentanza in edificio moderno nel quartiere Porta Nuova, vista skyline mozzafiato.	3500.00	\N	200	AFFITTO	Milano, Via Vittor Pisani 12	45.4814	9.2027	f	2	3	2026-04-04 00:14:53.232094	APPROVATO	2	5	0
70	Locale commerciale Napoli centro	Locale storico nel cuore di Napoli, alto soffitto, doppia vetrina su strada trafficatissima.	1800.00	\N	95	AFFITTO	Napoli, Via Toledo 120	40.8389	14.2494	f	2	4	2026-04-04 00:14:53.232094	APPROVATO	1	2	0
71	Box auto Torino zona centro	Box auto singolo in garage sotterraneo con videosorveglianza, a 200 metri dalla stazione Porta Nuova.	22000.00	\N	14	VENDITA	Torino, Via Nizza 5	45.0592	7.6762	f	2	5	2026-04-04 00:14:53.232094	APPROVATO	0	1	0
72	Terreno edificabile Lago di Garda	Terreno con permesso a costruire rilasciato, a 500 metri dal lago, vista panoramica garantita.	220000.00	\N	1200	VENDITA	Desenzano del Garda, Via Lungolago 3	45.4672	10.5362	f	2	6	2026-04-04 00:14:53.232094	APPROVATO	0	0	0
73	Appartamento bilocale Bologna universitaria	Bilocale ristrutturato in zona universitaria, arredato, vicino ai mezzi e alle facoltà.	780.00	\N	48	AFFITTO	Bologna, Via Zamboni 34	44.4978	11.3531	f	2	1	2026-04-04 00:14:53.232094	APPROVATO	1	2	0
74	Villa moderna Sicilia sul mare	Villa contemporanea a 50 metri dal mare, con piscina a sfioro, domotica e giardino tropicale.	680000.00	\N	260	VENDITA	Taormina, Via Lungomare 8	37.8516	15.2873	f	2	2	2026-04-04 00:14:53.232094	APPROVATO	\N	\N	1
75	Appartamento trilocale Firenze	Trilocale in palazzo d epoca nel centro storico di Firenze, travi a vista e pavimenti originali.	320000.00	\N	105	VENDITA	Firenze, Via dei Servi 22	43.7763	11.2586	f	2	1	2026-04-04 00:14:53.232094	APPROVATO	1	3	0
76	Ufficio Genova porto antico	Ufficio con vista sul porto antico di Genova, open space modulabile, fibra ottica inclusa.	1400.00	\N	140	AFFITTO	Genova, Via al Porto Antico 6	44.4071	8.9337	f	2	3	2026-04-04 00:14:53.232094	APPROVATO	1	3	0
77	Locale commerciale Venezia	Locale commerciale in calle frequentatissima a Venezia, ideale per negozio o galleria d arte.	2200.00	\N	55	AFFITTO	Venezia, Calle dei Fabbri 45	45.4345	12.3381	f	2	4	2026-04-04 00:14:53.232094	APPROVATO	1	1	0
79	Box auto doppio Verona	Doppio box auto in condominio recente con impianto elettrico, zona fiera di Verona.	35000.00	\N	30	VENDITA	Verona, Via Fiera 12	45.4384	10.9916	f	2	5	2026-04-04 00:14:53.232094	APPROVATO	0	1	0
80	Terreno agricolo Umbria	Terreno agricolo in Umbria con casale da ristrutturare, vigneto e uliveto, strada privata di accesso.	180000.00	\N	5000	VENDITA	Orvieto, Strada della Chiana	42.7165	12.1073	f	2	6	2026-04-04 00:14:53.232094	APPROVATO	0	0	0
81	Villa bifamiliare Sardegna	Villa bifamiliare a 200 metri dalla spiaggia di Porto Cervo, giardino privato e posto barca.	1200000.00	\N	230	VENDITA	Arzachena, Via Costa Smeralda 7	41.0843	9.3883	f	2	2	2026-04-04 00:14:53.232094	APPROVATO	3	5	0
82	Appartamento monolocale Roma Trastevere	Monolocale caratteristico nel cuore di Trastevere, soffitti alti, mattoni a vista, arredato con cura.	1100.00	\N	35	AFFITTO	Roma, Via della Lungaretta 14	41.8896	12.4703	f	2	1	2026-04-04 00:14:53.232094	APPROVATO	1	1	0
83	Ufficio coworking Torino	Spazio coworking moderno in palazzo ristrutturato, sala conferenze, cucina condivisa e terrazza.	550.00	\N	80	AFFITTO	Torino, Via Garibaldi 22	45.0712	7.6858	f	2	3	2026-04-04 00:14:53.232094	APPROVATO	1	4	0
84	Locale commerciale Milano Navigli	Locale storico sui Navigli milanesi, doppia vetrina, cantina e bagno. Zona ad altissimo passaggio.	2800.00	\N	80	AFFITTO	Milano, Via Naviglio Grande 34	45.4491	9.1726	f	2	4	2026-04-04 00:14:53.232094	APPROVATO	1	2	0
85	Appartamento quadrilocale Palermo	Quadrilocale signorile in palazzo liberty con portone d epoca, parquet e doppi servizi.	195000.00	\N	135	VENDITA	Palermo, Via Libertà 55	38.1254	13.3615	f	2	1	2026-04-04 00:14:53.232094	APPROVATO	2	4	0
86	Terreno edificabile Lago di Como	Terreno con progetto approvato per villa unifamiliare, vista lago di Como e montagne circostanti.	350000.00	\N	900	VENDITA	Cernobbio, Via Bellagio 3	45.8412	9.0734	f	2	6	2026-04-04 00:14:53.232094	APPROVATO	0	0	0
66	Terreno edificabile vista mare	Terreno con progetto approvato per villetta bifamiliare, splendida vista sul mare e facile accesso.	130000.00	\N	800	VENDITA	Scalea, Via Marina	39.8234	15.7912	f	15	6	2026-04-04 00:13:06.007623	APPROVATO	\N	\N	7
78	Attico panoramico Bari	Attico con terrazza a 360 gradi sul centro di Bari, completamente ristrutturato con materiali pregiati.	380000.00	\N	150	VENDITA	Bari, Via Sparano 88	41.1253	16.8666	f	2	1	2026-04-04 00:14:53.232094	APPROVATO	\N	\N	1
\.


--
-- TOC entry 5146 (class 0 OID 24764)
-- Dependencies: 232
-- Data for Name: asta; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.asta (id, id_annuncio, prezzo_base, offerta_max, id_offerente, data_scadenza, attiva) FROM stdin;
3	3	24000.00	\N	\N	2026-03-25 14:05:00	f
4	3	24000.00	\N	\N	2026-03-25 14:05:00	f
1	1	200000.00	\N	\N	2026-03-29 01:51:00	f
2	1	200000.00	\N	\N	2026-03-29 01:51:00	f
5	30	45000.00	\N	\N	2026-03-28 15:25:00	f
6	30	40000.00	\N	\N	2026-03-19 17:52:00	f
7	30	40000.00	\N	\N	2026-03-28 15:52:00	f
8	30	40000.00	\N	\N	2026-03-13 13:53:00	t
9	1	200000.00	\N	\N	2026-03-28 14:17:00	f
10	1	200000.00	\N	\N	2026-03-28 17:21:00	f
11	1	200000.00	\N	\N	2026-03-28 17:23:00	f
12	1	200000.00	\N	\N	2026-03-28 16:23:00	f
13	1	200000.00	\N	\N	2026-03-28 17:34:00	f
14	30	40000.00	\N	\N	2026-04-11 17:10:00	t
\.


--
-- TOC entry 5134 (class 0 OID 24634)
-- Dependencies: 220
-- Data for Name: categoria; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categoria (id, nome, ordine) FROM stdin;
1	Appartamento/Casa	1
2	Villa	2
5	Ufficio	3
6	Locale commerciale	4
3	Box auto	5
4	Terreno edificabile	6
\.


--
-- TOC entry 5140 (class 0 OID 24695)
-- Dependencies: 226
-- Data for Name: foto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.foto (id, id_annuncio, url) FROM stdin;
14	10	http://localhost:8080/uploads/7d17773f-44be-455b-8b4a-591d553f596d.jpg
15	10	http://localhost:8080/uploads/e27b3984-7715-4f2e-b799-f2461b5762f9.jpg
16	10	http://localhost:8080/uploads/f6859d44-b2c9-4e2a-ad88-1677fc2c8731.jpg
25	47	https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800
26	47	https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800
27	48	https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800
28	48	https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800
29	49	https://images.unsplash.com/photo-1497366216548-37526070297c?w=800
30	49	https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=800
31	50	https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800
32	50	https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?w=800
34	51	https://images.unsplash.com/photo-1486006920555-c77dcf18193c?w=800
35	52	https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800
36	52	https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800
37	53	https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800
38	53	https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=800
39	54	https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800
40	54	https://images.unsplash.com/photo-1523217582562-09d0def993a6?w=800
41	55	https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800
42	55	https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=800
43	56	https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800
44	56	https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800
45	57	https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800
46	57	https://images.unsplash.com/photo-1528698827591-e19ccd7bc23d?w=800
49	59	https://images.unsplash.com/photo-1590674899484-d5640e854abe?w=800
50	59	https://images.unsplash.com/photo-1486006920555-c77dcf18193c?w=800
51	60	https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800
52	60	https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=800
55	62	https://images.unsplash.com/photo-1536376072261-38c75010e6c9?w=800
56	62	https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800
57	63	https://images.unsplash.com/photo-1497366811353-6870744d04b2?w=800
58	63	https://images.unsplash.com/photo-1462826303086-329426d1aef5?w=800
59	64	https://images.unsplash.com/photo-1567401893414-76b7b1e5a7a5?w=800
60	64	https://images.unsplash.com/photo-1441984904996-e0b6ba687e04?w=800
61	65	https://images.unsplash.com/photo-1560185127-6ed189bf02f4?w=800
62	65	https://images.unsplash.com/photo-1554995207-c18c203602cb?w=800
65	67	https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800
66	67	https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800
67	68	https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800
69	69	https://images.unsplash.com/photo-1497366216548-37526070297c?w=800
70	69	https://images.unsplash.com/photo-1542744173-8e7e53415bb0?w=800
71	70	https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800
72	70	https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?w=800
73	71	https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800
74	71	https://images.unsplash.com/photo-1506521781263-d8422e82f27a?w=800
75	72	https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800
76	72	https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=800
77	73	https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800
78	73	https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=800
81	75	https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800
82	75	https://images.unsplash.com/photo-1560185007-cde436f6a4d0?w=800
83	76	https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800
84	76	https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=800
85	77	https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800
86	77	https://images.unsplash.com/photo-1528698827591-e19ccd7bc23d?w=800
89	79	https://images.unsplash.com/photo-1590674899484-d5640e854abe?w=800
90	79	https://images.unsplash.com/photo-1486006920555-c77dcf18193c?w=800
91	80	https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=800
92	80	https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800
93	81	https://images.unsplash.com/photo-1583608205776-bfd35f0d9f83?w=800
95	82	https://images.unsplash.com/photo-1536376072261-38c75010e6c9?w=800
96	82	https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800
97	83	https://images.unsplash.com/photo-1497366811353-6870744d04b2?w=800
98	83	https://images.unsplash.com/photo-1462826303086-329426d1aef5?w=800
99	84	https://images.unsplash.com/photo-1567401893414-76b7b1e5a7a5?w=800
100	84	https://images.unsplash.com/photo-1441984904996-e0b6ba687e04?w=800
101	85	https://images.unsplash.com/photo-1560185127-6ed189bf02f4?w=800
102	85	https://images.unsplash.com/photo-1554995207-c18c203602cb?w=800
104	86	https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=800
105	1	https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800
106	1	https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800
107	2	https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800
108	2	https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800
110	3	https://images.unsplash.com/photo-1486006920555-c77dcf18193c?w=800
111	4	https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800
112	4	https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=800
113	5	https://images.unsplash.com/photo-1497366216548-37526070297c?w=800
114	5	https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=800
115	6	https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=800
116	6	https://images.unsplash.com/photo-1583608205776-bfd35f0d9f83?w=800
117	7	https://images.unsplash.com/photo-1590674899484-d5640e854abe?w=800
118	7	https://images.unsplash.com/photo-1506521781263-d8422e82f27a?w=800
119	8	https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800
121	9	https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800
122	9	https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800
123	11	https://images.unsplash.com/photo-1536376072261-38c75010e6c9?w=800
124	11	https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800
125	12	https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800
127	14	https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800
128	14	https://images.unsplash.com/photo-1523217582562-09d0def993a6?w=800
129	15	https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800
130	15	https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800
131	16	https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800
132	16	https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=800
133	17	https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=800
134	17	https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800
135	18	https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800
136	18	https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=800
137	19	https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800
138	19	https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=800
139	20	https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800
140	20	https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800
141	21	https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=800
142	21	https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=800
143	22	https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800
144	22	https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800
145	23	https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800
146	23	https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=800
147	24	https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800
148	24	https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=800
149	25	https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=800
150	25	https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800
151	26	https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800
152	26	https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800
153	27	https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800
154	27	https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=800
155	28	https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800
156	28	https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=800
159	32	https://images.unsplash.com/photo-1505118380757-91f5f5632de0?w=800
161	74	http://localhost:8080/uploads/845eb3f9-c165-43ad-b401-692b5d847331.jpg
162	30	http://localhost:8080/uploads/15371dc5-4f37-4671-87b9-e16ae818eb9b.jpg
163	30	http://localhost:8080/uploads/0d43b454-6dc3-46c2-990f-1ebc95a7ed77.jpg
164	61	http://localhost:8080/uploads/4113dcd1-a3fc-4d65-b429-c76cf18e12d4.jpg
166	58	http://localhost:8080/uploads/e0a7f746-594d-4e5e-affa-22df010b9fa8.jpg
167	32	http://localhost:8080/uploads/499c2313-d0f7-4644-8945-e62ea21faf98.jpg
168	66	http://localhost:8080/uploads/773cf564-c2db-4a38-ba87-9f6eb583cbc9.jpg
169	78	http://localhost:8080/uploads/79f54ef2-418c-47bc-832b-db4c19e46e9e.jpg
170	87	http://localhost:8080/uploads/ba13df3c-ec75-4d88-a2e4-b0cced8524f6.jpg
172	88	http://localhost:8080/uploads/00aee883-3a23-4d95-b019-6488e540793e.jpg
\.


--
-- TOC entry 5144 (class 0 OID 24738)
-- Dependencies: 230
-- Data for Name: messaggio; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.messaggio (id, id_annuncio, id_mittente, oggetto, testo, data_invio, letto, eliminato_venditore, eliminato_acquirente, per_admin) FROM stdin;
35	28	2	Nuova richiesta di approvazione	Il venditore Mario Rossi ha richiesto l'approvazione dell'annuncio: "t".	2026-03-28 00:58:44.565182	t	f	f	t
54	\N	3	Richiesta promozione a venditore	L'utente Giulia Bianchi (giulia.bianchi@email.it) ha richiesto di diventare venditore.	2026-03-28 18:19:06.397397	t	f	f	t
37	30	2	Nuova richiesta di approvazione	Il venditore Mario Rossi ha richiesto l'approvazione dell'annuncio: "Appartamento in riva al mare".	2026-03-28 11:26:45.360809	t	f	f	t
55	\N	1	✅ Sei diventato venditore!	Congratulazioni! La tua richiesta è stata approvata. Ora puoi pubblicare annunci sulla piattaforma.	2026-03-28 18:19:39.51477	f	f	f	f
154	87	1	✅ Annuncio approvato!	Il tuo annuncio "Chalet in montagna" è stato approvato ed è ora visibile a tutti gli utenti.	2026-04-04 12:21:12.060498	t	f	f	f
56	\N	5	Richiesta promozione a venditore	L'utente giulia lentino (giulia.lentino@gmail.com) ha richiesto di diventare venditore.	2026-03-28 18:37:40.777999	t	f	f	t
57	\N	1	❌ Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 18:38:27.696061	f	f	f	f
58	\N	5	Richiesta promozione a venditore	L'utente giulia lentino (giulia.lentino@gmail.com) ha richiesto di diventare venditore.	2026-03-28 18:38:53.782428	t	f	f	t
59	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 18:54:42.413851	f	f	f	f
34	16	2	Nuova richiesta di approvazione	Il venditore Mario Rossi ha richiesto l'approvazione dell'annuncio: "terreno".	2026-03-28 00:24:06.847359	t	t	f	f
60	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 18:54:49.44653	f	f	f	f
61	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 18:54:55.005698	f	f	f	f
47	32	2	Nuova richiesta di approvazione	Il venditore Mario Rossi ha richiesto l'approvazione dell'annuncio: "casa vancanze".	2026-03-28 15:05:47.281005	t	f	f	t
31	4	3	Interesse per: Appartamento in affitto	Salve, sono interessato/a all'annuncio "Appartamento in affitto". Contattatemi via email (giulia.bianchi@email.it) per ulteriori informazioni. Grazie.	2026-03-27 20:01:21.180542	t	t	f	f
33	5	3	Interesse per: Ufficio commerciale	Salve, sono interessato/a all'annuncio "Ufficio commerciale". Contattatemi via email (giulia.bianchi@email.it) per ulteriori informazioni. Grazie.	2026-03-27 20:12:58.096039	t	t	f	f
32	7	3	Interesse per: Box auto centro	Salve, sono interessato/a all'annuncio "Box auto centro". Contattatemi via email (giulia.bianchi@email.it) per ulteriori informazioni. Grazie.	2026-03-27 20:06:38.357417	t	t	f	f
62	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 18:54:58.706815	f	f	f	f
50	\N	3	Richiesta promozione a venditore	L'utente Giulia Bianchi (giulia.bianchi@email.it) ha richiesto di diventare venditore.	2026-03-28 18:12:32.774444	t	f	f	t
51	\N	1	❌ Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 18:13:19.765782	f	f	f	f
52	\N	3	Richiesta promozione a venditore	L'utente Giulia Bianchi (giulia.bianchi@email.it) ha richiesto di diventare venditore.	2026-03-28 18:13:40.338198	t	f	f	t
53	\N	1	❌ Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 18:17:51.494684	f	f	f	f
63	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 18:55:03.126647	f	f	f	f
64	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 18:55:06.210809	f	f	f	f
65	\N	5	Richiesta promozione a venditore	L'utente giulia lentino (giulia.lentino@gmail.com) ha richiesto di diventare venditore.	2026-03-28 18:55:53.238908	t	f	f	t
66	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 18:56:22.02177	f	f	f	f
67	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 18:56:27.632503	f	f	f	f
68	\N	5	Richiesta promozione a venditore	L'utente giulia lentino (giulia.lentino@gmail.com) ha richiesto di diventare venditore.	2026-03-28 18:56:59.582766	t	f	f	t
69	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 18:57:46.549061	f	f	f	f
70	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 18:59:16.307096	f	f	f	f
71	\N	5	Richiesta promozione a venditore	L'utente giulia lentino (giulia.lentino@gmail.com) ha richiesto di diventare venditore.	2026-03-28 19:06:46.772137	t	f	f	t
72	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 19:07:36.492194	f	f	f	f
73	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 19:07:40.836862	f	f	f	f
74	\N	5	Richiesta promozione a venditore	L'utente giulia lentino (giulia.lentino@gmail.com) ha richiesto di diventare venditore.	2026-03-28 19:08:49.173892	t	f	f	t
75	\N	1	Sei diventato venditore!	Congratulazioni giulia! La tua richiesta è stata approvata. Ora puoi pubblicare annunci sulla piattaforma.	2026-03-28 19:09:14.659051	f	f	f	f
76	\N	6	Richiesta promozione a venditore	L'utente bob alice (bobalice@email.it) ha richiesto di diventare venditore.	2026-03-28 19:16:55.361458	t	f	f	t
77	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 19:17:17.637308	f	f	f	f
78	\N	6	Richiesta promozione a venditore	L'utente bob alice (bobalice@email.it) ha richiesto di diventare venditore.	2026-03-28 19:17:40.333593	t	f	f	t
79	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 19:18:06.733428	f	f	f	f
49	32	1	✅ Annuncio approvato!	Il tuo annuncio "casa vancanze" è stato approvato ed è ora visibile a tutti gli utenti.	2026-03-28 17:59:11.595497	t	t	f	f
82	\N	6	Richiesta promozione a venditore	L'utente bob alice (bobalice@email.it) ha richiesto di diventare venditore.	2026-03-28 22:20:47.796529	t	f	f	t
83	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-03-28 22:21:10.822474	f	f	f	f
84	7	6	Interesse per: Box auto centro	Salve, sono interessato/a all'annuncio "Box auto centro". Contattatemi via email (bobalice@email.it) per ulteriori informazioni. Grazie.	2026-03-29 15:30:15.240832	t	f	f	f
152	32	3	⚠️ Segnalazione annuncio	L'utente Giulia Bianchi ha segnalato l'annuncio: "casa vancanze".\n\nCategoria: CONTENUTO_FALSO\nMotivo: .	2026-04-03 23:59:51.001542	t	f	f	t
155	67	6	Interesse per: Appartamento vista Colosseo	Salve, sono interessato/a all'annuncio "Appartamento vista Colosseo". Contattatemi via email (bobalice@email.it) per ulteriori informazioni. Grazie.	2026-04-04 12:30:40.224373	t	f	f	f
158	88	6	⚠️ Segnalazione annuncio	L'utente bob alice ha segnalato l'annuncio: "Casa in affitto".\n\nCategoria: TRUFFA\nMotivo: possibile truffa	2026-04-04 12:45:12.612847	t	f	f	t
93	\N	6	Richiesta promozione a venditore	L'utente bob alice (bobalice@email.it) ha richiesto di diventare venditore.	2026-04-02 22:02:31.181377	t	f	f	t
94	\N	1	Richiesta rifiutata	La tua richiesta di diventare venditore è stata rifiutata. Contatta l'amministratore per maggiori informazioni.	2026-04-02 22:03:09.590296	f	f	f	f
95	10	6	⚠️ Segnalazione annuncio	L'utente bob alice ha segnalato l'annuncio: "Villa con piscina e giardino".\n\nCategoria: CONTENUTO_FALSO\nMotivo: Non esiste	2026-04-03 16:54:09.662325	t	f	f	t
96	32	6	⚠️ Segnalazione annuncio	L'utente bob alice ha segnalato l'annuncio: "casa vancanze".\n\nCategoria: ALTRO\nMotivo: posizione non reale 	2026-04-03 17:29:41.976359	t	f	f	t
98	30	6	⚠️ Segnalazione annuncio	L'utente bob alice ha segnalato l'annuncio: "Appartamento in riva al mare".\n\nCategoria: TRUFFA\nMotivo: .	2026-04-03 17:38:32.176155	t	f	f	t
100	32	15	⚠️ Segnalazione annuncio	L'utente Giulia Lentino ha segnalato l'annuncio: "casa vancanze".\n\nCategoria: PREZZO_ERRATO\nMotivo: .	2026-04-03 20:04:32.797935	t	f	f	t
102	15	15	⚠️ Segnalazione annuncio	L'utente Giulia Lentino ha segnalato l'annuncio: "terreno ".\n\nCategoria: ALTRO\nMotivo: .	2026-04-03 20:11:11.127242	t	f	f	t
104	30	15	⚠️ Segnalazione annuncio	L'utente Giulia Lentino ha segnalato l'annuncio: "Appartamento in riva al mare".\n\nCategoria: ALTRO\nMotivo: .	2026-04-03 20:14:47.867203	t	f	f	t
111	1	15	⚠️ Segnalazione annuncio	L'utente Giulia Lentino ha segnalato l'annuncio: "Appartamento luminoso centro".\n\nCategoria: ALTRO\nMotivo: .	2026-04-03 21:25:22.881328	t	f	f	t
115	10	15	⚠️ Segnalazione annuncio	L'utente Giulia Lentino ha segnalato l'annuncio: "Villa con piscina e giardino".\n\nCategoria: ALTRO\nMotivo: .	2026-04-03 21:47:57.417625	t	f	f	t
117	8	15	⚠️ Segnalazione annuncio	L'utente Giulia Lentino ha segnalato l'annuncio: "Appartamento in affitto".\n\nCategoria: ALTRO\nMotivo: .	2026-04-03 21:51:10.21141	t	f	f	t
119	3	15	⚠️ Segnalazione annuncio	L'utente Giulia Lentino ha segnalato l'annuncio: "Box auto centro".\n\nCategoria: TRUFFA\nMotivo: .	2026-04-03 21:55:41.347619	t	f	f	t
118	8	15	Segnalazione inviata	Hai segnalato l'annuncio: "Appartamento in affitto".\nCategoria: ALTRO\nMotivo: .\n\nL'amministratore esaminerà la segnalazione al più presto.	2026-04-03 21:51:10.213998	t	t	f	f
116	10	15	Segnalazione inviata	Hai segnalato l'annuncio: "Villa con piscina e giardino".\nCategoria: ALTRO\nMotivo: .\n\nL'amministratore esaminerà la segnalazione al più presto.	2026-04-03 21:47:57.419132	t	t	f	f
112	1	15	Segnalazione inviata	Hai segnalato l'annuncio: "Appartamento luminoso centro".\nCategoria: ALTRO\nMotivo: .\n\nL'amministratore esaminerà la segnalazione al più presto.	2026-04-03 21:25:22.883916	t	t	f	f
105	30	15	✅ Segnalazione inviata	Hai segnalato l'annuncio: "Appartamento in riva al mare".\nCategoria: ALTRO\nMotivo: .\n\nL'amministratore esaminerà la segnalazione al più presto.	2026-04-03 20:14:47.871836	t	t	f	f
103	15	15	✅ Segnalazione inviata	Hai segnalato l'annuncio: "terreno ".\nCategoria: ALTRO\nMotivo: .\n\nL'amministratore esaminerà la segnalazione al più presto.	2026-04-03 20:11:11.132181	t	t	f	f
101	32	15	✅ Segnalazione inviata	Hai segnalato l'annuncio: "casa vancanze".\nCategoria: PREZZO_ERRATO\nMotivo: .\n\nL'amministratore esaminerà la segnalazione al più presto.	2026-04-03 20:04:32.803649	t	t	f	f
99	30	6	✅ Segnalazione inviata	Hai segnalato l'annuncio: "Appartamento in riva al mare".\nCategoria: TRUFFA\nMotivo: .\n\nL'amministratore esaminerà la segnalazione al più presto.	2026-04-03 17:38:32.178826	t	t	f	f
97	32	6	✅ Segnalazione inviata	Hai segnalato l'annuncio: "casa vancanze".\nCategoria: ALTRO\nMotivo: posizione non reale \n\nL'amministratore esaminerà la segnalazione al più presto.	2026-04-03 17:29:41.996785	t	t	f	f
120	3	15	Segnalazione inviata	Hai segnalato l'annuncio: "Box auto centro".\nCategoria: TRUFFA\nMotivo: .\n\nL'amministratore esaminerà la segnalazione al più presto.	2026-04-03 21:55:41.349533	t	t	f	f
125	10	5	⚠️ Segnalazione annuncio	L'utente giulia lentino ha segnalato l'annuncio: "Villa con piscina e giardino".\n\nCategoria: TRUFFA\nMotivo: .	2026-04-03 22:10:59.232742	t	f	f	t
127	2	15	⚠️ Segnalazione annuncio	L'utente Giulia Lentino ha segnalato l'annuncio: "Villa con piscina".\n\nCategoria: FOTO_NON_REALI\nMotivo: .	2026-04-03 22:17:01.125852	t	f	f	t
153	87	3	Nuova richiesta di approvazione	Il venditore Giulia Bianchi ha richiesto l'approvazione dell'annuncio: "Villa in montagna".	2026-04-04 12:20:19.298043	t	f	f	t
128	2	15	Segnalazione inviata	Hai segnalato l'annuncio: "Villa con piscina".\nCategoria: FOTO_NON_REALI\nMotivo: .\n\nL'amministratore esaminerà la segnalazione al più presto.	2026-04-03 22:17:01.188361	t	t	f	f
126	10	5	Segnalazione inviata	Hai segnalato l'annuncio: "Villa con piscina e giardino".\nCategoria: TRUFFA\nMotivo: .\n\nL'amministratore esaminerà la segnalazione al più presto.	2026-04-03 22:10:59.234213	t	t	f	f
156	88	2	Nuova richiesta di approvazione	Il venditore Mario Rossi ha richiesto l'approvazione dell'annuncio: "Casa in affitto".	2026-04-04 12:36:44.160195	t	f	f	t
157	88	1	✅ Annuncio approvato!	Il tuo annuncio "Casa in affitto" è stato approvato ed è ora visibile a tutti gli utenti.	2026-04-04 12:37:47.254886	t	f	f	f
137	4	5	⚠️ Segnalazione annuncio	L'utente giulia lentino ha segnalato l'annuncio: "Appartamento in affitto".\n\nCategoria: CONTENUTO_FALSO\nMotivo: .	2026-04-03 22:46:45.079163	t	f	f	t
138	5	5	⚠️ Segnalazione annuncio	L'utente giulia lentino ha segnalato l'annuncio: "Ufficio commerciale".\n\nCategoria: TRUFFA\nMotivo: .	2026-04-03 22:46:57.959392	t	f	f	t
143	\N	3	✅ Segnalazione inviata	Hai segnalato l'annuncio: "Casa in affitto a Trieste".\nCategoria: CONTENUTO_FALSO\nMotivo: .\n\nL'amministratore esaminerà la segnalazione al più presto.	2026-04-03 23:19:47.239574	f	f	f	f
\.


--
-- TOC entry 5148 (class 0 OID 24787)
-- Dependencies: 234
-- Data for Name: offerta; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.offerta (id, id_asta, id_utente, importo, data_offerta) FROM stdin;
\.


--
-- TOC entry 5150 (class 0 OID 24810)
-- Dependencies: 236
-- Data for Name: preferito; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.preferito (id, id_utente, id_annuncio) FROM stdin;
\.


--
-- TOC entry 5142 (class 0 OID 24712)
-- Dependencies: 228
-- Data for Name: recensione; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.recensione (id, id_annuncio, id_utente, punteggio, commento, data_rec) FROM stdin;
1	1	3	4	Ottimo appartamento, molto luminoso!	2026-03-21 19:19:24.284309
\.


--
-- TOC entry 5152 (class 0 OID 24844)
-- Dependencies: 238
-- Data for Name: richiesta_promozione; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.richiesta_promozione (id, id_utente, stato, data_richiesta) FROM stdin;
2	3	RIFIUTATO	2026-03-28 18:19:06.332023
5	5	APPROVATO	2026-03-28 19:08:48.922318
11	6	RIFIUTATO	2026-04-02 22:02:31.175325
\.


--
-- TOC entry 5154 (class 0 OID 33037)
-- Dependencies: 240
-- Data for Name: segnalazione; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.segnalazione (id, id_annuncio, id_segnalante, motivo, categoria, stato, data_inserimento) FROM stdin;
1	10	6	Non esiste	CONTENUTO_FALSO	GESTITA	2026-04-03 16:54:09.651204
3	30	6	.	TRUFFA	GESTITA	2026-04-03 17:38:32.159179
2	32	6	posizione non reale 	ALTRO	GESTITA	2026-04-03 17:29:41.971423
4	32	15	.	PREZZO_ERRATO	GESTITA	2026-04-03 20:04:32.767875
5	15	15	.	ALTRO	GESTITA	2026-04-03 20:11:11.116786
6	30	15	.	ALTRO	GESTITA	2026-04-03 20:14:47.85633
7	1	15	.	ALTRO	GESTITA	2026-04-03 21:25:22.873388
8	10	15	.	ALTRO	GESTITA	2026-04-03 21:47:57.404626
9	8	15	.	ALTRO	GESTITA	2026-04-03 21:51:10.198255
10	3	15	.	TRUFFA	GESTITA	2026-04-03 21:55:41.337151
12	10	5	.	TRUFFA	GESTITA	2026-04-03 22:10:59.229329
13	2	15	.	FOTO_NON_REALI	GESTITA	2026-04-03 22:17:01.112581
20	5	5	.	TRUFFA	GESTITA	2026-04-03 22:46:57.950352
19	4	5	.	CONTENUTO_FALSO	GESTITA	2026-04-03 22:46:45.073829
29	32	3	.	CONTENUTO_FALSO	GESTITA	2026-04-03 23:59:50.988365
30	88	6	possibile truffa	TRUFFA	GESTITA	2026-04-04 12:45:12.589542
\.


--
-- TOC entry 5136 (class 0 OID 24645)
-- Dependencies: 222
-- Data for Name: utente; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.utente (id, nome, cognome, email, password, ruolo, bannato, foto_profilo, email_verificata, token_verifica, token_reset, token_reset_scadenza) FROM stdin;
1	Admin	Sistema	immobiliareunical@gmail.com	$2a$04$y2A9gIzs9hCkUFbqLnzf7OGMMfyo/G2/uv4pxrXNYj.pi/6x0hgre	AMMINISTRATORE	f	http://localhost:8080/uploads/profilo_1_18f3c3f6-20e7-4231-a4bb-7c7b057bff24.jpg	t	\N	\N	\N
15	Giulia	Lentino	lentinogiulia@gmail.com	$2a$04$BIhWJUdijLfAZgapjVYjBusUM0sVzDyuMQv31UvEWHEymvu421noi	VENDITORE	f	http://localhost:8080/uploads/profilo_15_f78b52a4-3f62-4e49-81ff-1504ca4a6b1b.jpg	t	\N	e4a2e967-bde5-41b3-8b7e-9116d9ef980a	2026-03-31 16:08:58.981635
2	Mario	Rossi	mario.rossi@email.it	$2a$04$Q1Jn7smCMeZT./ZOp77dGOv04hkMHRQGzXjBWlj16bW.HpsQkUUmK	VENDITORE	f	http://localhost:8080/uploads/profilo_2_a6122249-b6c3-421f-a50b-09034ab84578.jpg	t	\N	\N	\N
6	bob	alice	bobalice@email.it	$2a$04$uSMMJaMcNfl1iS5WAy9oI.DR9cQUVLjAE4Gl82jTYNKakuQtjTDP6	ACQUIRENTE	f	http://localhost:8080/uploads/profilo_6_92f20043-19d7-4f9d-a9f4-d4da197ba8df.jpg	t	\N	\N	\N
3	Giulia	Bianchi	giulia.bianchi@email.it	$2a$04$dXsOQIuY6maHewBIvrrI6u0MqZZ4fOlllVQRbq9KoQV0e.rdlM21W	VENDITORE	f	\N	t	\N	\N	\N
4	Test	Utente	test@test.it	$2a$04$CJM42wzeGI/21c8nDBSoGOWDzP/v2O6ASdUNB67ssTDQY6gZqQIpG	AMMINISTRATORE	t	\N	t	\N	\N	\N
7	ciao	ciao	giulia.lentino@email.it	$2a$04$Xfx2d1rmCNDV8wsuIU166O5TM4WiJvkccbIasHEY3US0E9MN5WYQO	ACQUIRENTE	f	\N	t	\N	\N	\N
5	giulia	lentino	giulia.lentino@gmail.com	$2a$04$C1Qf3S/7MqACSOVijiX/4uTDcsWioUynH4GtRvVJB6FnHDADqqRBO	VENDITORE	f	\N	t	\N	\N	\N
\.


--
-- TOC entry 5171 (class 0 OID 0)
-- Dependencies: 223
-- Name: annuncio_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.annuncio_id_seq', 88, true);


--
-- TOC entry 5172 (class 0 OID 0)
-- Dependencies: 231
-- Name: asta_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.asta_id_seq', 14, true);


--
-- TOC entry 5173 (class 0 OID 0)
-- Dependencies: 219
-- Name: categoria_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.categoria_id_seq', 6, true);


--
-- TOC entry 5174 (class 0 OID 0)
-- Dependencies: 225
-- Name: foto_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.foto_id_seq', 172, true);


--
-- TOC entry 5175 (class 0 OID 0)
-- Dependencies: 229
-- Name: messaggio_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.messaggio_id_seq', 158, true);


--
-- TOC entry 5176 (class 0 OID 0)
-- Dependencies: 233
-- Name: offerta_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.offerta_id_seq', 1, false);


--
-- TOC entry 5177 (class 0 OID 0)
-- Dependencies: 235
-- Name: preferito_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.preferito_id_seq', 5, true);


--
-- TOC entry 5178 (class 0 OID 0)
-- Dependencies: 227
-- Name: recensione_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.recensione_id_seq', 1, true);


--
-- TOC entry 5179 (class 0 OID 0)
-- Dependencies: 237
-- Name: richiesta_promozione_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.richiesta_promozione_id_seq', 14, true);


--
-- TOC entry 5180 (class 0 OID 0)
-- Dependencies: 239
-- Name: segnalazione_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.segnalazione_id_seq', 30, true);


--
-- TOC entry 5181 (class 0 OID 0)
-- Dependencies: 221
-- Name: utente_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.utente_id_seq', 15, true);


--
-- TOC entry 4949 (class 2606 OID 24683)
-- Name: annuncio annuncio_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.annuncio
    ADD CONSTRAINT annuncio_pkey PRIMARY KEY (id);


--
-- TOC entry 4957 (class 2606 OID 24775)
-- Name: asta asta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.asta
    ADD CONSTRAINT asta_pkey PRIMARY KEY (id);


--
-- TOC entry 4940 (class 2606 OID 24643)
-- Name: categoria categoria_nome_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categoria
    ADD CONSTRAINT categoria_nome_key UNIQUE (nome);


--
-- TOC entry 4942 (class 2606 OID 24641)
-- Name: categoria categoria_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categoria
    ADD CONSTRAINT categoria_pkey PRIMARY KEY (id);


--
-- TOC entry 4951 (class 2606 OID 24705)
-- Name: foto foto_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.foto
    ADD CONSTRAINT foto_pkey PRIMARY KEY (id);


--
-- TOC entry 4955 (class 2606 OID 24752)
-- Name: messaggio messaggio_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messaggio
    ADD CONSTRAINT messaggio_pkey PRIMARY KEY (id);


--
-- TOC entry 4959 (class 2606 OID 24798)
-- Name: offerta offerta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offerta
    ADD CONSTRAINT offerta_pkey PRIMARY KEY (id);


--
-- TOC entry 4961 (class 2606 OID 24820)
-- Name: preferito preferito_id_utente_id_annuncio_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preferito
    ADD CONSTRAINT preferito_id_utente_id_annuncio_key UNIQUE (id_utente, id_annuncio);


--
-- TOC entry 4963 (class 2606 OID 24818)
-- Name: preferito preferito_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preferito
    ADD CONSTRAINT preferito_pkey PRIMARY KEY (id);


--
-- TOC entry 4953 (class 2606 OID 24726)
-- Name: recensione recensione_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.recensione
    ADD CONSTRAINT recensione_pkey PRIMARY KEY (id);


--
-- TOC entry 4965 (class 2606 OID 24854)
-- Name: richiesta_promozione richiesta_promozione_id_utente_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.richiesta_promozione
    ADD CONSTRAINT richiesta_promozione_id_utente_key UNIQUE (id_utente);


--
-- TOC entry 4967 (class 2606 OID 24852)
-- Name: richiesta_promozione richiesta_promozione_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.richiesta_promozione
    ADD CONSTRAINT richiesta_promozione_pkey PRIMARY KEY (id);


--
-- TOC entry 4969 (class 2606 OID 33049)
-- Name: segnalazione segnalazione_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.segnalazione
    ADD CONSTRAINT segnalazione_pkey PRIMARY KEY (id);


--
-- TOC entry 4945 (class 2606 OID 24663)
-- Name: utente utente_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utente
    ADD CONSTRAINT utente_email_key UNIQUE (email);


--
-- TOC entry 4947 (class 2606 OID 24661)
-- Name: utente utente_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utente
    ADD CONSTRAINT utente_pkey PRIMARY KEY (id);


--
-- TOC entry 4943 (class 1259 OID 24860)
-- Name: idx_utente_email; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_utente_email ON public.utente USING btree (email);


--
-- TOC entry 4970 (class 2606 OID 24689)
-- Name: annuncio annuncio_id_categoria_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.annuncio
    ADD CONSTRAINT annuncio_id_categoria_fkey FOREIGN KEY (id_categoria) REFERENCES public.categoria(id);


--
-- TOC entry 4971 (class 2606 OID 24684)
-- Name: annuncio annuncio_id_venditore_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.annuncio
    ADD CONSTRAINT annuncio_id_venditore_fkey FOREIGN KEY (id_venditore) REFERENCES public.utente(id);


--
-- TOC entry 4977 (class 2606 OID 24776)
-- Name: asta asta_id_annuncio_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.asta
    ADD CONSTRAINT asta_id_annuncio_fkey FOREIGN KEY (id_annuncio) REFERENCES public.annuncio(id) ON DELETE CASCADE;


--
-- TOC entry 4978 (class 2606 OID 24781)
-- Name: asta asta_id_offerente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.asta
    ADD CONSTRAINT asta_id_offerente_fkey FOREIGN KEY (id_offerente) REFERENCES public.utente(id);


--
-- TOC entry 4972 (class 2606 OID 24706)
-- Name: foto foto_id_annuncio_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.foto
    ADD CONSTRAINT foto_id_annuncio_fkey FOREIGN KEY (id_annuncio) REFERENCES public.annuncio(id) ON DELETE CASCADE;


--
-- TOC entry 4975 (class 2606 OID 24753)
-- Name: messaggio messaggio_id_annuncio_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messaggio
    ADD CONSTRAINT messaggio_id_annuncio_fkey FOREIGN KEY (id_annuncio) REFERENCES public.annuncio(id) ON DELETE CASCADE;


--
-- TOC entry 4976 (class 2606 OID 24758)
-- Name: messaggio messaggio_id_mittente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messaggio
    ADD CONSTRAINT messaggio_id_mittente_fkey FOREIGN KEY (id_mittente) REFERENCES public.utente(id);


--
-- TOC entry 4979 (class 2606 OID 24799)
-- Name: offerta offerta_id_asta_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offerta
    ADD CONSTRAINT offerta_id_asta_fkey FOREIGN KEY (id_asta) REFERENCES public.asta(id) ON DELETE CASCADE;


--
-- TOC entry 4980 (class 2606 OID 24804)
-- Name: offerta offerta_id_utente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offerta
    ADD CONSTRAINT offerta_id_utente_fkey FOREIGN KEY (id_utente) REFERENCES public.utente(id);


--
-- TOC entry 4981 (class 2606 OID 24826)
-- Name: preferito preferito_id_annuncio_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preferito
    ADD CONSTRAINT preferito_id_annuncio_fkey FOREIGN KEY (id_annuncio) REFERENCES public.annuncio(id) ON DELETE CASCADE;


--
-- TOC entry 4982 (class 2606 OID 24821)
-- Name: preferito preferito_id_utente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preferito
    ADD CONSTRAINT preferito_id_utente_fkey FOREIGN KEY (id_utente) REFERENCES public.utente(id) ON DELETE CASCADE;


--
-- TOC entry 4973 (class 2606 OID 24727)
-- Name: recensione recensione_id_annuncio_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.recensione
    ADD CONSTRAINT recensione_id_annuncio_fkey FOREIGN KEY (id_annuncio) REFERENCES public.annuncio(id) ON DELETE CASCADE;


--
-- TOC entry 4974 (class 2606 OID 24732)
-- Name: recensione recensione_id_utente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.recensione
    ADD CONSTRAINT recensione_id_utente_fkey FOREIGN KEY (id_utente) REFERENCES public.utente(id);


--
-- TOC entry 4983 (class 2606 OID 24855)
-- Name: richiesta_promozione richiesta_promozione_id_utente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.richiesta_promozione
    ADD CONSTRAINT richiesta_promozione_id_utente_fkey FOREIGN KEY (id_utente) REFERENCES public.utente(id) ON DELETE CASCADE;


--
-- TOC entry 4984 (class 2606 OID 33050)
-- Name: segnalazione segnalazione_id_annuncio_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.segnalazione
    ADD CONSTRAINT segnalazione_id_annuncio_fkey FOREIGN KEY (id_annuncio) REFERENCES public.annuncio(id) ON DELETE CASCADE;


--
-- TOC entry 4985 (class 2606 OID 33055)
-- Name: segnalazione segnalazione_id_segnalante_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.segnalazione
    ADD CONSTRAINT segnalazione_id_segnalante_fkey FOREIGN KEY (id_segnalante) REFERENCES public.utente(id) ON DELETE CASCADE;


-- Completed on 2026-04-04 12:54:44

--
-- PostgreSQL database dump complete
--

\unrestrict S3JWmTikT0zGbhu67ltoLXLNNvVRO1WvcKbj7iNBIC5kQUqxvbo4t8Ip0KY7ZE3

