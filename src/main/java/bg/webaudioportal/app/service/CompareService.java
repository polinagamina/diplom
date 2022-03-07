package bg.webaudioportal.app.service;

import bg.webaudioportal.app.model.MIDINote;
import bg.webaudioportal.app.model.ResultOfCompare;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CompareService {
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    private static final double COEFFICIENT_A = 1.3;
    private static final double COEFFICIENT_B = -5293570.29;
    private static final String MAGENTA_FOLDER = "Magenta/";
    private static final String ORIGINAL_SONGS_FOLDER = "Original/";

    public static List<ResultOfCompare> compareMidiFiles(String fileName) throws IOException, InvalidMidiDataException {
        File magentaFile = new File(MAGENTA_FOLDER + fileName + ".midi");
        File originalFile = new File(ORIGINAL_SONGS_FOLDER + fileName + "_Original.mid");
        List<MIDINote> originalSong = new ArrayList<>();
        originalSong = makeNote(originalFile.getAbsolutePath());
        originalSong = originalSong.stream().sorted(Comparator.comparingLong(MIDINote::getStartMS)).collect(Collectors.toList());
        System.out.println("magenta song");
        List<MIDINote> magentaSong = new ArrayList<>();
        magentaSong = makeNote(magentaFile.getAbsolutePath());
        magentaSong = magentaSong.stream().sorted(Comparator.comparingLong(MIDINote::getStartMS)).collect(Collectors.toList());
        for (MIDINote midiNote : magentaSong) {
            double timeStartToOriginal = COEFFICIENT_A * midiNote.getStartMS() + COEFFICIENT_B;
            midiNote.setStartMS((long) timeStartToOriginal);
            double timeEndToOriginal = COEFFICIENT_A * midiNote.getEndMS() + COEFFICIENT_B;
            midiNote.setEndMS((long) timeEndToOriginal);
        }


        List<ResultOfCompare> listAfterComparison = new ArrayList<>();
        for (MIDINote note : originalSong) {
            List<MIDINote> sortNotes = new ArrayList<>();
            sortNotes = magentaSong.stream().filter(sortNote -> sortNote.getStartMS() >= note.getStartMS() - 200000 && sortNote.getStartMS() <= note.getStartMS() + 200000 && sortNote.getNoteName().equals(note.getNoteName())).collect(Collectors.toList());
            if (sortNotes.size() != 0) {
                ResultOfCompare resultOfCompare = new ResultOfCompare(note.getNoteName(), "Right Note", note.getStartMS());
                listAfterComparison.add(resultOfCompare);
                // System.out.println("True" + note.getNoteName() + note.getStartMS());
            } else {
                ResultOfCompare resultOfCompareFalse = new ResultOfCompare(note.getNoteName(), "Wrong Note", note.getStartMS());
                listAfterComparison.add(resultOfCompareFalse);
                //System.out.println("False");
            }
        }
        listAfterComparison = listAfterComparison.stream().sorted(Comparator.comparingLong(ResultOfCompare::getStartMS)).collect(Collectors.toList());
        return listAfterComparison;
    }

    public static ArrayList<MIDINote> makeNote(String musicPath) throws IOException, InvalidMidiDataException {

        Sequence sequence = MidiSystem.getSequence(new File(musicPath));
        int mpq = 60000000 / 100;
        int seqres = sequence.getResolution();
        long lasttick = 0;
        long curtime = 0;
        float divtype = sequence.getDivisionType();

        ArrayList<MIDINote> notes = new ArrayList<>();
        int trackNumber = 0;
        for (Track track : sequence.getTracks()) {
            trackNumber++;
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                if (divtype == Sequence.PPQ) {
                    curtime += ((event.getTick() - lasttick) * mpq) / seqres;
                } else {
                    curtime = (long) ((event.getTick() * 1000000.0 * divtype) / seqres);

                }
                lasttick = event.getTick();
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;

                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        MIDINote midiNote = new MIDINote(curtime, 0, noteName, octave, note, velocity);
                        if (velocity != 0) {
                            notes.add(midiNote);
                        } else {
                            List<MIDINote> sortNotes = new ArrayList<>();
                            sortNotes = notes.stream().filter(sortMidiNote -> sortMidiNote.getEndMS() == 0 && sortMidiNote.getNoteName().equals(noteName) && sortMidiNote.getOctave() == octave).collect(Collectors.toList());
                            sortNotes.get(0).setEndMS(curtime);
                        }

                    } else if (sm.getCommand() == NOTE_OFF) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        if (notes != null) {
                            List<MIDINote> sortNotes = new ArrayList<>();
                            sortNotes = notes.stream().filter(midiNote -> midiNote.getEndMS() == 0 && midiNote.getNoteName().equals(noteName) && midiNote.getOctave() == octave).collect(Collectors.toList());
                            sortNotes.get(0).setEndMS(curtime);
                        }

                    }
                }
            }


        }
        return notes;
    }
}
