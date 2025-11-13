create table "Estados"
(
    "idEstado" integer not null,
    nombre     text    not null,
    constraint "PK_idEstado"
        primary key ("idEstado")
);

alter table "Estados"
    owner to postgres;

create table "Contenedores"
(
    "idContenedor" integer generated always as identity,
    peso           double precision not null,
    volumen        double precision not null,
    "idEstado"     integer          not null,
    constraint "PK_idContenedor"
        primary key ("idContenedor"),
    constraint "FK_idEstado"
        foreign key ("idEstado") references "Estados"
);

alter table "Contenedores"
    owner to postgres;

create table "Clientes"
(
    dni       text not null,
    nombre    text not null,
    apellido  text not null,
    correo    text,
    telefono  text,
    direccion text not null,
    constraint "PK_dni"
        primary key (dni)
);

alter table "Clientes"
    owner to postgres;

create table "Solicitudes"
(
    "idSolicitud"    integer generated always as identity,
    "idContenedor"   integer not null,
    "dniCliente"     text    not null,
    "costoEstimado"  double precision,
    "tiempoEstimado" double precision,
    "costoFinal"     double precision,
    "tiempoReal"     double precision,
    "idEstado"       integer not null,
    constraint "PK_idSolicitud"
        primary key ("idSolicitud"),
    constraint "FK_idContenedor"
        foreign key ("idContenedor") references "Contenedores",
    constraint "FK_dniCliente"
        foreign key ("dniCliente") references "Clientes",
    constraint "FK_idEstado"
        foreign key ("idEstado") references "Estados"
);

alter table "Solicitudes"
    owner to postgres;

create table "TiposCamion"
(
    "idTipoCamion" integer not null,
    nombre         text    not null,
    constraint "PK_idTipoCamion"
        primary key ("idTipoCamion")
);

alter table "TiposCamion"
    owner to postgres;

create table "Transportistas"
(
    "idTransportista" integer generated always as identity,
    nombre            text not null,
    apellido          text not null,
    constraint "PK_idTransportista"
        primary key ("idTransportista")
);

alter table "Transportistas"
    owner to postgres;

create table "Camiones"
(
    patente            text             not null,
    "idTransportista"  integer,
    "idTipoCamion"     integer          not null,
    "capacidadPeso"    double precision not null,
    "capacidadVolumen" double precision not null,
    disponibilidad     boolean          not null,
    constraint "PK_patente"
        primary key (patente),
    constraint "FK_idTransportista"
        foreign key ("idTransportista") references "Transportistas",
    constraint "FK_idTipoCamion"
        foreign key ("idTipoCamion") references "TiposCamion"
);

alter table "Camiones"
    owner to postgres;

create table "Tarifas"
(
    "idTarifa"                  integer generated always as identity,
    "costoBaseXKm"              double precision,
    "valorLitroCombustible"     double precision,
    "consumoCombustibleGeneral" double precision,
    "idTipoCamion"              integer not null,
    constraint "PK_idTarifa"
        primary key ("idTarifa"),
    constraint "FK_idTipoCamion"
        foreign key ("idTipoCamion") references "TiposCamion"
);

comment on column "Tarifas"."costoBaseXKm" is 'valorLitroCombustible * consumoCombustibleGeneral';

comment on column "Tarifas"."consumoCombustibleGeneral" is 'litros consumidos por km';

alter table "Tarifas"
    owner to postgres;

create table "Ubicaciones"
(
    "idUbicacion" integer generated always as identity,
    latitud       double precision not null,
    longitud      double precision not null,
    constraint "PK_idUbicacion"
        primary key ("idUbicacion")
);

alter table "Ubicaciones"
    owner to postgres;

create table "Depositos"
(
    "idDeposito"   integer generated always as identity,
    "idUbicacion"  integer          not null,
    "costoEstadia" double precision not null,
    constraint "PK_idDeposito"
        primary key ("idDeposito"),
    constraint "FK_idUbicacion"
        foreign key ("idUbicacion") references "Ubicaciones"
);

alter table "Depositos"
    owner to postgres;

create table "TiposTramo"
(
    "idTipotramo" integer not null,
    nombre        text    not null,
    constraint "PK_idTipoTramo"
        primary key ("idTipotramo")
);

alter table "TiposTramo"
    owner to postgres;

create table "Rutas"
(
    "idRuta"             integer generated always as identity,
    "cantidadTramos"     integer not null,
    "cantidadDepositos"  integer not null,
    "idUbicacionInicial" integer not null,
    "idUbicacionFinal"   integer not null,
    "idSolicitud"        integer not null,
    constraint "PK_idRuta"
        primary key ("idRuta"),
    constraint "FK_idUbicacionInicial"
        foreign key ("idUbicacionInicial") references "Ubicaciones",
    constraint "FK_idUbicacionFinal"
        foreign key ("idUbicacionFinal") references "Ubicaciones",
    constraint "FK_idSolicitud"
        foreign key ("idSolicitud") references "Solicitudes"
);

alter table "Rutas"
    owner to postgres;

create table "Tramos"
(
    "idTramo"                 integer generated always as identity,
    "idRuta"                  integer          not null,
    "idUbicacionOrigen"       integer          not null,
    "idUbicacionDestino"      integer          not null,
    distancia                 double precision not null,
    "idTipoTramo"             integer          not null,
    "idEstado"                integer          not null,
    "costoAproximado"         double precision,
    "costoReal"               double precision,
    "fechaHoraInicioEstimada" timestamp,
    "fechaHoraFinEstimada"    timestamp,
    "fechaHoraInicio"         timestamp,
    "fechaHoraFin"            timestamp,
    "patenteCamion"           text,
    constraint pk_idtramo
        primary key ("idTramo"),
    constraint "FK_idRuta"
        foreign key ("idRuta") references "Rutas",
    constraint "FK_idUbicacionOrigen"
        foreign key ("idUbicacionOrigen") references "Ubicaciones",
    constraint "FK_idUbicacionDestino"
        foreign key ("idUbicacionDestino") references "Ubicaciones",
    constraint "FK_idTipoTramo"
        foreign key ("idTipoTramo") references "TiposTramo",
    constraint "FK_idEstado"
        foreign key ("idEstado") references "Estados",
    constraint "FK_patenteCamion"
        foreign key ("patenteCamion") references "Camiones"
);

alter table "Tramos"
    owner to postgres;


/* CARGA DE DATOS INICIALES A LAS TABLAS */

INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-49.5824, -71.40593);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-47.01746, -67.25345);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-45.4678, -69.82696);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-43.48773, -70.81339);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-43.25729, -65.29922);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-41.1679, -71.31376);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-40.84259, -68.09423);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-39.50139, -62.66852);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-38.70003, -62.24179);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-39.30389, -65.64836);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-38.89489, -70.06694);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-37.7658, -67.72022);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-37.37722, -64.60111);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-37.95337, -57.57943);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-36.77313, -59.86781);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-35.98044, -62.71897);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-35.86717, -69.80332);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-34.64242, -68.3278);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-33.68881, -65.466);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-33.13084, -64.33936);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-33.73874, -61.96646);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-34.72502, -58.25594);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-32.15873, -58.39933);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-30.35973, -60.39729);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-31.43337, -64.15694);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-31.52037, -68.54065);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-28.921, -67.5192);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-28.46857, -62.85004);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-29.19194, -58.07352);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-27.46014, -55.82609);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-27.49787, -58.63929);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-26.50055, -61.19839);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-27.22068, -66.82485);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-25.43963, -66.28373);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-23.57253, -65.39748);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-23.89737, -63.19907);
INSERT INTO public."Ubicaciones" (latitud, longitud) VALUES (-24.69958, -60.59406);


INSERT INTO public."Transportistas" (nombre, apellido) VALUES ('Jhon', 'Sismo');
INSERT INTO public."Transportistas" (nombre, apellido) VALUES ('Carlitos', 'Camión');
INSERT INTO public."Transportistas" (nombre, apellido) VALUES ('Transpo', 'Rtista');
INSERT INTO public."Transportistas" (nombre, apellido) VALUES ('Michael', 'Jackson');
INSERT INTO public."Transportistas" (nombre, apellido) VALUES ('Lautaro', 'Vasquez');


INSERT INTO public."TiposTramo" ("idTipotramo", nombre) VALUES (1, 'origen-destino');
INSERT INTO public."TiposTramo" ("idTipotramo", nombre) VALUES (2, 'origen-deposito');
INSERT INTO public."TiposTramo" ("idTipotramo", nombre) VALUES (3, 'deposito-deposito');
INSERT INTO public."TiposTramo" ("idTipotramo", nombre) VALUES (4, 'deposito-destino');


INSERT INTO public."TiposCamion" ("idTipoCamion", nombre) VALUES (1, 'carga seca');
INSERT INTO public."TiposCamion" ("idTipoCamion", nombre) VALUES (2, 'refrigerado');
INSERT INTO public."TiposCamion" ("idTipoCamion", nombre) VALUES (3, 'plataforma');
INSERT INTO public."TiposCamion" ("idTipoCamion", nombre) VALUES (4, 'cortina lateral');
INSERT INTO public."TiposCamion" ("idTipoCamion", nombre) VALUES (5, 'cisterna');
INSERT INTO public."TiposCamion" ("idTipoCamion", nombre) VALUES (6, 'cama baja');


INSERT INTO public."Estados" ("idEstado", nombre) VALUES (1, 'pendiente');
INSERT INTO public."Estados" ("idEstado", nombre) VALUES (2, 'en camino');
INSERT INTO public."Estados" ("idEstado", nombre) VALUES (3, 'finalizado');
INSERT INTO public."Estados" ("idEstado", nombre) VALUES (4, 'borrador');
INSERT INTO public."Estados" ("idEstado", nombre) VALUES (5, 'confirmada');
INSERT INTO public."Estados" ("idEstado", nombre) VALUES (6, 'en proceso');
INSERT INTO public."Estados" ("idEstado", nombre) VALUES (7, 'finalizada');
INSERT INTO public."Estados" ("idEstado", nombre) VALUES (8, 'por retirar');
INSERT INTO public."Estados" ("idEstado", nombre) VALUES (9, 'en viaje');
INSERT INTO public."Estados" ("idEstado", nombre) VALUES (10, 'en deposito');
INSERT INTO public."Estados" ("idEstado", nombre) VALUES (11, 'entregado');


INSERT INTO public."Clientes" (dni, nombre, apellido, correo, telefono, direccion) VALUES ('45690591', 'Ignacio', 'Llabot', 'ignallabot@gmail.com', '351111111', 'Calle 1234');
INSERT INTO public."Clientes" (dni, nombre, apellido, correo, telefono, direccion) VALUES ('11222333', 'Francisco', 'Salvatico', 'fsalvatico@gmail.com', '351222222', 'Avenida 4567');


INSERT INTO public."Camiones" (patente, "idTransportista", "idTipoCamion", "capacidadPeso", "capacidadVolumen", disponibilidad) VALUES ('AD434PY', 1, 1, 1500, 3000, true);
INSERT INTO public."Camiones" (patente, "idTransportista", "idTipoCamion", "capacidadPeso", "capacidadVolumen", disponibilidad) VALUES ('AA163TY', 2, 2, 1600, 4000, true);
INSERT INTO public."Camiones" (patente, "idTransportista", "idTipoCamion", "capacidadPeso", "capacidadVolumen", disponibilidad) VALUES ('AB987CJ', 3, 3, 1700, 5000, true);
INSERT INTO public."Camiones" (patente, "idTransportista", "idTipoCamion", "capacidadPeso", "capacidadVolumen", disponibilidad) VALUES ('AC111BC', 4, 4, 1800, 6000, true);
INSERT INTO public."Camiones" (patente, "idTransportista", "idTipoCamion", "capacidadPeso", "capacidadVolumen", disponibilidad) VALUES ('AE698HI', 5, 5, 1900, 7000, true);


INSERT INTO public."Tarifas" ("costoBaseXKm", "valorLitroCombustible", "consumoCombustibleGeneral", "idTipoCamion") VALUES (300, 1500, 0.2, 1);
INSERT INTO public."Tarifas" ("costoBaseXKm", "valorLitroCombustible", "consumoCombustibleGeneral", "idTipoCamion") VALUES (387.5, 1550, 0.25, 2);
INSERT INTO public."Tarifas" ("costoBaseXKm", "valorLitroCombustible", "consumoCombustibleGeneral", "idTipoCamion") VALUES (480, 1600, 0.3, 3);
INSERT INTO public."Tarifas" ("costoBaseXKm", "valorLitroCombustible", "consumoCombustibleGeneral", "idTipoCamion") VALUES (612.5, 1750, 0.35, 4);
INSERT INTO public."Tarifas" ("costoBaseXKm", "valorLitroCombustible", "consumoCombustibleGeneral", "idTipoCamion") VALUES (720, 1800, 0.4, 5);
INSERT INTO public."Tarifas" ("costoBaseXKm", "valorLitroCombustible", "consumoCombustibleGeneral", "idTipoCamion") VALUES (832.5, 1850, 0.45, 6);


/* Acá puede que haya que cambiar las idUbicacion para que vayan del 1 al 37. PD si hay que hacerlo ya lo hago. Listo */
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (1, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (2, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (3, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (4, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (5, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (6, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (7, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (8, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (9, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (10, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (11, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (12, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (13, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (14, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (15, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (16, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (17, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (18, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (19, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (20, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (21, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (22, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (23, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (24, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (25, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (26, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (27, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (28, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (29, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (30, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (31, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (32, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (33, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (34, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (35, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (36, 0);
INSERT INTO public."Depositos" ("idUbicacion", "costoEstadia") VALUES (37, 0);
