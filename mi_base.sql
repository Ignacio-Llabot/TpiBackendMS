--
-- PostgreSQL database dump
--

\restrict Kg7cEweG35eGIvqSIeaXUUEZVtJjTkaJgcrpLGA4bRvrP95g8LdxugEjdmupmKs

-- Dumped from database version 18.0
-- Dumped by pg_dump version 18.0

-- Started on 2025-11-06 09:51:34

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
-- TOC entry 225 (class 1259 OID 16501)
-- Name: Camiones; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Camiones" (
    patente text NOT NULL,
    "idTransportista" integer,
    "idTipoCamion" integer NOT NULL,
    telefono text NOT NULL,
    "capacidadPeso" double precision NOT NULL,
    "capacidadVolumen" double precision NOT NULL,
    disponibilidad boolean NOT NULL,
    costos double precision NOT NULL,
    "costoBaseXKm" double precision NOT NULL,
    "consumoPromCombustible" double precision NOT NULL
);


ALTER TABLE public."Camiones" OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16445)
-- Name: Clientes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Clientes" (
    dni text NOT NULL,
    nombre text NOT NULL,
    apellido text NOT NULL,
    correo text,
    telefono text,
    direccion text NOT NULL
);


ALTER TABLE public."Clientes" OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16431)
-- Name: Contenedores; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Contenedores" (
    "idContenedor" integer NOT NULL,
    peso double precision NOT NULL,
    volumen double precision NOT NULL,
    "idEstado" integer NOT NULL
);


ALTER TABLE public."Contenedores" OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 16550)
-- Name: Depositos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Depositos" (
    "idDeposito" integer NOT NULL,
    nombre text NOT NULL,
    "idUbicacion" integer NOT NULL,
    "costoEstadia" double precision NOT NULL
);


ALTER TABLE public."Depositos" OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16422)
-- Name: Estados; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Estados" (
    "idEstado" integer NOT NULL,
    nombre text NOT NULL
);


ALTER TABLE public."Estados" OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 16576)
-- Name: Rutas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Rutas" (
    "idRuta" integer NOT NULL,
    "cantidadTramos" integer NOT NULL,
    "cantidadDepositos" integer NOT NULL,
    "idUbicacionInicial" integer NOT NULL,
    "idUbicacionFinal" integer NOT NULL,
    "idSolicitud" integer NOT NULL
);


ALTER TABLE public."Rutas" OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16456)
-- Name: Solicitudes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Solicitudes" (
    "idSolicitud" integer NOT NULL,
    "idContenedor" integer NOT NULL,
    "dniCliente" text NOT NULL,
    "costoEstimado" double precision,
    "tiempoEstimado" interval,
    "costoFinal" double precision,
    "tiempoReal" interval,
    "idEstado" integer NOT NULL
);


ALTER TABLE public."Solicitudes" OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16527)
-- Name: Tarifas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Tarifas" (
    "idTarifa" integer NOT NULL,
    "costoBaseXKm" double precision,
    "valorLitroCombustible" double precision,
    "consumoCombustibleGeneral" double precision,
    "idTipoCamion" integer NOT NULL
);


ALTER TABLE public."Tarifas" OWNER TO postgres;

--
-- TOC entry 5111 (class 0 OID 0)
-- Dependencies: 226
-- Name: TABLE "Tarifas"; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public."Tarifas" IS 'no le puse los not null porque no entiendo como armamos esto
';


--
-- TOC entry 223 (class 1259 OID 16482)
-- Name: TiposCamion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."TiposCamion" (
    "idTipoCamion" integer NOT NULL,
    nombre text NOT NULL
);


ALTER TABLE public."TiposCamion" OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16566)
-- Name: TiposTramo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."TiposTramo" (
    "idTipotramo" integer NOT NULL,
    nombre text NOT NULL,
    descripcion text NOT NULL
);


ALTER TABLE public."TiposTramo" OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 16602)
-- Name: Tramos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Tramos" (
    "idTramo" integer NOT NULL,
    "idRuta" integer NOT NULL,
    "idUbicacionOrigen" integer NOT NULL,
    "idUbicacionDestino" integer NOT NULL,
    distancia double precision NOT NULL,
    "idTipoTramo" integer NOT NULL,
    "idEstado" integer NOT NULL,
    "costoAproximado" double precision NOT NULL,
    "costoReal" double precision,
    "fechaHoraInicioEstimada" timestamp without time zone NOT NULL,
    "fechaHoraFinEstimada" timestamp without time zone NOT NULL,
    "fechaHoraInicio" timestamp without time zone,
    "fechaHoraFin" timestamp without time zone,
    "patenteCamion" text NOT NULL
);


ALTER TABLE public."Tramos" OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16491)
-- Name: Transportistas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Transportistas" (
    "idTransportista" integer NOT NULL,
    nombre text NOT NULL,
    apellido text NOT NULL
);


ALTER TABLE public."Transportistas" OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16539)
-- Name: Ubicaciones; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Ubicaciones" (
    "idUbicacion" integer NOT NULL,
    direccion text NOT NULL,
    latitud double precision NOT NULL,
    longitud double precision NOT NULL
);


ALTER TABLE public."Ubicaciones" OWNER TO postgres;

--
-- TOC entry 5099 (class 0 OID 16501)
-- Dependencies: 225
-- Data for Name: Camiones; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Camiones" (patente, "idTransportista", "idTipoCamion", telefono, "capacidadPeso", "capacidadVolumen", disponibilidad, costos, "costoBaseXKm", "consumoPromCombustible") FROM stdin;
\.


--
-- TOC entry 5095 (class 0 OID 16445)
-- Dependencies: 221
-- Data for Name: Clientes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Clientes" (dni, nombre, apellido, correo, telefono, direccion) FROM stdin;
\.


--
-- TOC entry 5094 (class 0 OID 16431)
-- Dependencies: 220
-- Data for Name: Contenedores; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Contenedores" ("idContenedor", peso, volumen, "idEstado") FROM stdin;
\.


--
-- TOC entry 5102 (class 0 OID 16550)
-- Dependencies: 228
-- Data for Name: Depositos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Depositos" ("idDeposito", nombre, "idUbicacion", "costoEstadia") FROM stdin;
\.


--
-- TOC entry 5093 (class 0 OID 16422)
-- Dependencies: 219
-- Data for Name: Estados; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Estados" ("idEstado", nombre) FROM stdin;
\.


--
-- TOC entry 5104 (class 0 OID 16576)
-- Dependencies: 230
-- Data for Name: Rutas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Rutas" ("idRuta", "cantidadTramos", "cantidadDepositos", "idUbicacionInicial", "idUbicacionFinal", "idSolicitud") FROM stdin;
\.


--
-- TOC entry 5096 (class 0 OID 16456)
-- Dependencies: 222
-- Data for Name: Solicitudes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Solicitudes" ("idSolicitud", "idContenedor", "dniCliente", "costoEstimado", "tiempoEstimado", "costoFinal", "tiempoReal", "idEstado") FROM stdin;
\.


--
-- TOC entry 5100 (class 0 OID 16527)
-- Dependencies: 226
-- Data for Name: Tarifas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Tarifas" ("idTarifa", "costoBaseXKm", "valorLitroCombustible", "consumoCombustibleGeneral", "idTipoCamion") FROM stdin;
\.


--
-- TOC entry 5097 (class 0 OID 16482)
-- Dependencies: 223
-- Data for Name: TiposCamion; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."TiposCamion" ("idTipoCamion", nombre) FROM stdin;
\.


--
-- TOC entry 5103 (class 0 OID 16566)
-- Dependencies: 229
-- Data for Name: TiposTramo; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."TiposTramo" ("idTipotramo", nombre, descripcion) FROM stdin;
\.


--
-- TOC entry 5105 (class 0 OID 16602)
-- Dependencies: 231
-- Data for Name: Tramos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Tramos" ("idTramo", "idRuta", "idUbicacionOrigen", "idUbicacionDestino", distancia, "idTipoTramo", "idEstado", "costoAproximado", "costoReal", "fechaHoraInicioEstimada", "fechaHoraFinEstimada", "fechaHoraInicio", "fechaHoraFin", "patenteCamion") FROM stdin;
\.


--
-- TOC entry 5098 (class 0 OID 16491)
-- Dependencies: 224
-- Data for Name: Transportistas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Transportistas" ("idTransportista", nombre, apellido) FROM stdin;
\.


--
-- TOC entry 5101 (class 0 OID 16539)
-- Dependencies: 227
-- Data for Name: Ubicaciones; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Ubicaciones" ("idUbicacion", direccion, latitud, longitud) FROM stdin;
\.


--
-- TOC entry 4908 (class 2606 OID 16455)
-- Name: Clientes PK_dni; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Clientes"
    ADD CONSTRAINT "PK_dni" PRIMARY KEY (dni);


--
-- TOC entry 4906 (class 2606 OID 16439)
-- Name: Contenedores PK_idContenedor; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Contenedores"
    ADD CONSTRAINT "PK_idContenedor" PRIMARY KEY ("idContenedor");


--
-- TOC entry 4922 (class 2606 OID 16560)
-- Name: Depositos PK_idDeposito; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Depositos"
    ADD CONSTRAINT "PK_idDeposito" PRIMARY KEY ("idDeposito");


--
-- TOC entry 4904 (class 2606 OID 16430)
-- Name: Estados PK_idEstado; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Estados"
    ADD CONSTRAINT "PK_idEstado" PRIMARY KEY ("idEstado");


--
-- TOC entry 4926 (class 2606 OID 16586)
-- Name: Rutas PK_idRuta; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Rutas"
    ADD CONSTRAINT "PK_idRuta" PRIMARY KEY ("idRuta");


--
-- TOC entry 4910 (class 2606 OID 16466)
-- Name: Solicitudes PK_idSolicitud; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Solicitudes"
    ADD CONSTRAINT "PK_idSolicitud" PRIMARY KEY ("idSolicitud");


--
-- TOC entry 4918 (class 2606 OID 16533)
-- Name: Tarifas PK_idTarifa; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Tarifas"
    ADD CONSTRAINT "PK_idTarifa" PRIMARY KEY ("idTarifa");


--
-- TOC entry 4912 (class 2606 OID 16490)
-- Name: TiposCamion PK_idTipoCamion; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."TiposCamion"
    ADD CONSTRAINT "PK_idTipoCamion" PRIMARY KEY ("idTipoCamion");


--
-- TOC entry 4924 (class 2606 OID 16575)
-- Name: TiposTramo PK_idTipoTramo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."TiposTramo"
    ADD CONSTRAINT "PK_idTipoTramo" PRIMARY KEY ("idTipotramo");


--
-- TOC entry 4928 (class 2606 OID 16619)
-- Name: Tramos PK_idTramo_idRuta; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Tramos"
    ADD CONSTRAINT "PK_idTramo_idRuta" PRIMARY KEY ("idTramo", "idRuta");


--
-- TOC entry 4914 (class 2606 OID 16500)
-- Name: Transportistas PK_idTransportista; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Transportistas"
    ADD CONSTRAINT "PK_idTransportista" PRIMARY KEY ("idTransportista");


--
-- TOC entry 4920 (class 2606 OID 16549)
-- Name: Ubicaciones PK_idUbicacion; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Ubicaciones"
    ADD CONSTRAINT "PK_idUbicacion" PRIMARY KEY ("idUbicacion");


--
-- TOC entry 4916 (class 2606 OID 16516)
-- Name: Camiones PK_patente; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Camiones"
    ADD CONSTRAINT "PK_patente" PRIMARY KEY (patente);


--
-- TOC entry 4930 (class 2606 OID 16472)
-- Name: Solicitudes FK_dniCliente; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Solicitudes"
    ADD CONSTRAINT "FK_dniCliente" FOREIGN KEY ("dniCliente") REFERENCES public."Clientes"(dni);


--
-- TOC entry 4931 (class 2606 OID 16467)
-- Name: Solicitudes FK_idContenedor; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Solicitudes"
    ADD CONSTRAINT "FK_idContenedor" FOREIGN KEY ("idContenedor") REFERENCES public."Contenedores"("idContenedor");


--
-- TOC entry 4929 (class 2606 OID 16440)
-- Name: Contenedores FK_idEstado; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Contenedores"
    ADD CONSTRAINT "FK_idEstado" FOREIGN KEY ("idEstado") REFERENCES public."Estados"("idEstado");


--
-- TOC entry 4932 (class 2606 OID 16477)
-- Name: Solicitudes FK_idEstado; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Solicitudes"
    ADD CONSTRAINT "FK_idEstado" FOREIGN KEY ("idEstado") REFERENCES public."Estados"("idEstado");


--
-- TOC entry 4940 (class 2606 OID 16640)
-- Name: Tramos FK_idEstado; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Tramos"
    ADD CONSTRAINT "FK_idEstado" FOREIGN KEY ("idEstado") REFERENCES public."Estados"("idEstado");


--
-- TOC entry 4941 (class 2606 OID 16620)
-- Name: Tramos FK_idRuta; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Tramos"
    ADD CONSTRAINT "FK_idRuta" FOREIGN KEY ("idRuta") REFERENCES public."Rutas"("idRuta");


--
-- TOC entry 4937 (class 2606 OID 16597)
-- Name: Rutas FK_idSolicitud; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Rutas"
    ADD CONSTRAINT "FK_idSolicitud" FOREIGN KEY ("idSolicitud") REFERENCES public."Solicitudes"("idSolicitud");


--
-- TOC entry 4933 (class 2606 OID 16522)
-- Name: Camiones FK_idTipoCamion; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Camiones"
    ADD CONSTRAINT "FK_idTipoCamion" FOREIGN KEY ("idTipoCamion") REFERENCES public."TiposCamion"("idTipoCamion");


--
-- TOC entry 4935 (class 2606 OID 16534)
-- Name: Tarifas FK_idTipoCamion; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Tarifas"
    ADD CONSTRAINT "FK_idTipoCamion" FOREIGN KEY ("idTipoCamion") REFERENCES public."TiposCamion"("idTipoCamion");


--
-- TOC entry 4942 (class 2606 OID 16635)
-- Name: Tramos FK_idTipoTramo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Tramos"
    ADD CONSTRAINT "FK_idTipoTramo" FOREIGN KEY ("idTipoTramo") REFERENCES public."TiposTramo"("idTipotramo");


--
-- TOC entry 4934 (class 2606 OID 16517)
-- Name: Camiones FK_idTransportista; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Camiones"
    ADD CONSTRAINT "FK_idTransportista" FOREIGN KEY ("idTransportista") REFERENCES public."Transportistas"("idTransportista");


--
-- TOC entry 4936 (class 2606 OID 16561)
-- Name: Depositos FK_idUbicacion; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Depositos"
    ADD CONSTRAINT "FK_idUbicacion" FOREIGN KEY ("idUbicacion") REFERENCES public."Ubicaciones"("idUbicacion");


--
-- TOC entry 4943 (class 2606 OID 16630)
-- Name: Tramos FK_idUbicacionDestino; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Tramos"
    ADD CONSTRAINT "FK_idUbicacionDestino" FOREIGN KEY ("idUbicacionDestino") REFERENCES public."Ubicaciones"("idUbicacion");


--
-- TOC entry 4938 (class 2606 OID 16592)
-- Name: Rutas FK_idUbicacionFinal; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Rutas"
    ADD CONSTRAINT "FK_idUbicacionFinal" FOREIGN KEY ("idUbicacionFinal") REFERENCES public."Ubicaciones"("idUbicacion");


--
-- TOC entry 4939 (class 2606 OID 16587)
-- Name: Rutas FK_idUbicacionInicial; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Rutas"
    ADD CONSTRAINT "FK_idUbicacionInicial" FOREIGN KEY ("idUbicacionInicial") REFERENCES public."Ubicaciones"("idUbicacion");


--
-- TOC entry 4944 (class 2606 OID 16625)
-- Name: Tramos FK_idUbicacionOrigen; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Tramos"
    ADD CONSTRAINT "FK_idUbicacionOrigen" FOREIGN KEY ("idUbicacionOrigen") REFERENCES public."Ubicaciones"("idUbicacion");


--
-- TOC entry 4945 (class 2606 OID 16645)
-- Name: Tramos FK_patenteCamion; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Tramos"
    ADD CONSTRAINT "FK_patenteCamion" FOREIGN KEY ("patenteCamion") REFERENCES public."Camiones"(patente);


-- Completed on 2025-11-06 09:51:34

--
-- PostgreSQL database dump complete
--

\unrestrict Kg7cEweG35eGIvqSIeaXUUEZVtJjTkaJgcrpLGA4bRvrP95g8LdxugEjdmupmKs

